#!/usr/bin/env python3
"""Small authenticated WebSocket relay for CycloneSkies emote start/stop events.

Install: python -m pip install websockets
Run:     CYCLONE_RELAY_TOKEN=<long-random-secret> python versions/tools/emote_relay.py

For public hosting, terminate TLS at a reverse proxy and expose wss:// on port 9469.
The relay binds 0.0.0.0:9469 by default; clients connect to the configured public endpoint.
"""

from __future__ import annotations

import asyncio
import json
import logging
import os
import ssl
import re
import time
import uuid
from collections import defaultdict, deque
from dataclasses import dataclass, field
from typing import Any

from websockets.asyncio.server import ServerConnection, serve
from websockets.exceptions import ConnectionClosed

LOG = logging.getLogger("cycloneskies.emote_relay")
TOKEN = os.environ.get("CYCLONE_RELAY_TOKEN", "").strip()
HOST = os.environ.get("CYCLONE_RELAY_HOST", "0.0.0.0")
# Wispbyte and similar hosts usually inject PORT for the allocated public port.
PORT = int(os.environ.get("CYCLONE_RELAY_PORT", os.environ.get("PORT", "9469")))
TLS_CERT = os.environ.get("CYCLONE_RELAY_TLS_CERT", "").strip()
TLS_KEY = os.environ.get("CYCLONE_RELAY_TLS_KEY", "").strip()
MAX_MESSAGE_BYTES = 2048
MAX_CLIENTS = int(os.environ.get("CYCLONE_RELAY_MAX_CLIENTS", "256"))
ROOM_RE = re.compile(r"^[A-Za-z0-9_-]{1,64}$")
EMOTE_RE = re.compile(r"^[a-z0-9_-]{1,32}$")
RATE_LIMIT = 12
RATE_WINDOW = 10.0

# Keep synchronized with cyclone/src/main/java/dev/valence/emote/Emotes.java.
VALID_EMOTES = {
    "67", "cringe", "football", "redcard", "cozy", "kebab", "fivehead",
    "winner", "swagger", "caught4k", "bighead", "hanging", "pancake",
    "wheelie", "backflip", "headspin", "zen", "robot", "tpose",
}

@dataclass(eq=False)
class Peer:
    ws: ServerConnection
    room: str
    player_id: str
    display_name: str
    sent_events: deque[float] = field(default_factory=deque)

ROOMS: dict[str, set[Peer]] = defaultdict(set)
ROOM_ACTIVE: dict[str, dict[str, dict[str, Any]]] = defaultdict(dict)
CLIENTS: set[ServerConnection] = set()

async def send_json(ws: ServerConnection, payload: dict[str, Any]) -> None:
    await ws.send(json.dumps(payload, separators=(",", ":")))

async def broadcast(room: str, payload: dict[str, Any], *, exclude: ServerConnection | None = None) -> None:
    peers = tuple(ROOMS.get(room, ()))
    if not peers:
        return
    results = await asyncio.gather(
        *(send_json(peer.ws, payload) for peer in peers if peer.ws is not exclude),
        return_exceptions=True,
    )
    for peer, result in zip((p for p in peers if p.ws is not exclude), results):
        if isinstance(result, Exception):
            LOG.debug("send failed for peer %s: %s", peer.player_id, result)

async def reject(ws: ServerConnection, reason: str) -> None:
    try:
        await send_json(ws, {"type": "error", "reason": reason})
        await ws.close(code=1008, reason=reason[:120])
    except ConnectionClosed:
        pass

async def handler(ws: ServerConnection) -> None:
    if not TOKEN:
        await reject(ws, "relay is not configured")
        return
    if len(CLIENTS) >= MAX_CLIENTS:
        await reject(ws, "server full")
        return

    CLIENTS.add(ws)
    peer: Peer | None = None
    try:
        async for raw in ws:
            if not isinstance(raw, str) or len(raw.encode("utf-8")) > MAX_MESSAGE_BYTES:
                await reject(ws, "invalid or oversized message")
                break
            try:
                msg = json.loads(raw)
            except json.JSONDecodeError:
                await reject(ws, "invalid JSON")
                break
            if not isinstance(msg, dict):
                await reject(ws, "message must be an object")
                break

            kind = msg.get("type")
            if peer is None:
                if kind != "join" or msg.get("token") != TOKEN:
                    await reject(ws, "authenticate with a join message first")
                    break
                room = msg.get("room")
                player_id = msg.get("player_id")
                display = msg.get("name", "Player")
                if not isinstance(room, str) or not ROOM_RE.fullmatch(room):
                    await reject(ws, "invalid room")
                    break
                try:
                    player_id = str(uuid.UUID(player_id))
                except (ValueError, TypeError, AttributeError):
                    await reject(ws, "player_id must be a UUID")
                    break
                if not isinstance(display, str) or not display.strip() or len(display) > 32:
                    await reject(ws, "invalid display name")
                    break
                peer = Peer(ws, room, player_id, display.strip())
                ROOMS[room].add(peer)
                # Share currently active emotes with a late joiner.
                active = [state for state in ROOM_ACTIVE.get(room, {}).values()]
                await send_json(ws, {"type": "ready", "room": room, "server_time_ms": int(time.time() * 1000), "active": active})
                LOG.info("join room=%s player=%s", room, player_id)
                continue

            now = time.monotonic()
            while peer.sent_events and now - peer.sent_events[0] > RATE_WINDOW:
                peer.sent_events.popleft()
            if len(peer.sent_events) >= RATE_LIMIT:
                await reject(ws, "rate limit exceeded")
                break

            if kind == "start":
                emote_id = msg.get("emote")
                try:
                    started_at = int(msg.get("started_at_ms"))
                except (TypeError, ValueError):
                    await reject(ws, "started_at_ms must be an integer")
                    break
                if not isinstance(emote_id, str) or emote_id not in VALID_EMOTES:
                    await reject(ws, "unknown emote")
                    break
                if abs(int(time.time() * 1000) - started_at) > 30_000:
                    await reject(ws, "start time outside allowed window")
                    break
                event = {
                    "type": "start", "player_id": peer.player_id,
                    "name": peer.display_name, "emote": emote_id,
                    "started_at_ms": started_at,
                }
            elif kind == "stop":
                event = {"type": "stop", "player_id": peer.player_id}
            else:
                await reject(ws, "unsupported event")
                break

            peer.sent_events.append(now)
            if kind == "start":
                ROOM_ACTIVE.setdefault(peer.room, {})[peer.player_id] = event
            else:
                ROOM_ACTIVE.get(peer.room, {}).pop(peer.player_id, None)
            await broadcast(peer.room, event, exclude=ws)
    except ConnectionClosed:
        pass
    except Exception:
        LOG.exception("relay connection error")
    finally:
        CLIENTS.discard(ws)
        if peer is not None:
            room_peers = ROOMS.get(peer.room)
            if room_peers is not None:
                room_peers.discard(peer)
                if not room_peers:
                    ROOMS.pop(peer.room, None)
            ROOM_ACTIVE.get(peer.room, {}).pop(peer.player_id, None)
            if not ROOM_ACTIVE.get(peer.room):
                ROOM_ACTIVE.pop(peer.room, None)
            await broadcast(peer.room, {"type": "stop", "player_id": peer.player_id}, exclude=ws)
            LOG.info("leave room=%s player=%s", peer.room, peer.player_id)

async def main() -> None:
    if len(TOKEN) < 32:
        raise SystemExit("Set CYCLONE_RELAY_TOKEN to a random secret of at least 32 characters.")
    logging.basicConfig(level=os.environ.get("LOG_LEVEL", "INFO"), format="%(asctime)s %(levelname)s %(message)s")
    tls_context = None
    if TLS_CERT or TLS_KEY:
        if not TLS_CERT or not TLS_KEY:
            raise SystemExit("Set both CYCLONE_RELAY_TLS_CERT and CYCLONE_RELAY_TLS_KEY.")
        tls_context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        tls_context.minimum_version = ssl.TLSVersion.TLSv1_2
        tls_context.load_cert_chain(TLS_CERT, TLS_KEY)
    async with serve(
        handler, HOST, PORT,
        ssl=tls_context,
        max_size=MAX_MESSAGE_BYTES,
        ping_interval=20,
        ping_timeout=20,
        close_timeout=5,
    ):
        LOG.info("emote relay listening on %s://%s:%d", "wss" if tls_context else "ws", HOST, PORT)
        await asyncio.Future()

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        pass
