# Payment Alerts — effect reference

46 effects — 45 amount tiers in ladder order plus one event tier. **Every stage effect is a dedicated class** under
`com.paymentalerts.alert` with its own timeline, geometry, motion, palette and sound
script. There is no shared preset renderer and no `EffectStyle` table left in the tree —
`PresetStageAlert` and `EffectStyle` were deleted once the last effect was ported.

`StageBase` is plumbing only: a clock, a particle buffer, a sound scheduler, the standard
banner, and a handful of draw primitives (`spawn`, `coin`, `bill`, `star4`, `heart`,
`heartRing`, `shaft`, `wash`, `band`). Each subclass implements `duration()` and `paint()`
and owns its look from first frame to last; some also override `cue()`, `advance()` and
`drawsBanner()`.

Legend:
- **Stage** — full-screen takeover, own beat sheet.
- **Side** — small anchored alert, own motion.
- **Event** — not on the amount ladder; only fired explicitly, by chat detection or command.

| # | id | Label | Threshold | Class | Kind | Looks |
|---|---|---|---|---|---|---|
| 1 | `coin_pop` | Coin pop | 1m | `CoinPopAlert` | Side | Normal / Gold / Silver |
| 2 | `tab` | Skin tab | 10m | `TabAlert` | Side | Normal / Silver / Gold |
| 3 | `coin_fountain` | Coin fountain | 25m | `CoinFountainAlert` | Side | — |
| 4 | `money_rain` | Money rain | 50m | `MoneyRainAlert` | Side | — |
| 5 | `sparkle_burst` | Sparkle burst | 75m | `SparkleBurstAlert` | Stage | Soft / Bright / Rainbow |
| 6 | `cash_cannon` | Cash cannon | 90m | `CashCannonAlert` | Stage | Single barrel / Double / Gatling |
| 7 | `fireworks` | Fireworks | 100m | `FireworksAlert` | Stage | Classic / Gold / Rainbow finale |
| 8 | `party_popper` | Party popper | 150m | `PartyPopperAlert` | Stage | Classic / Neon / Pastel |
| 9 | `confetti` | Confetti cannons | 200m | `ConfettiAlert` | Stage | Classic / Neon / Pastel |
| 10 | `heart_bomb` | Heart bomb | 220m | `HeartBombAlert` | Stage | Pink / Red / Rainbow |
| 11 | `lightning` | Lightning storm | 250m | `LightningAlert` | Stage | Classic / Supercell / Dry |
| 12 | `thunder_clap` | Thunder clap | 300m | `ThunderClapAlert` | Stage | Single / Rolling / God storm |
| 13 | `disco_ball` | Disco ball | 350m | `DiscoBallAlert` | Stage | 70s / Neon / Strobe |
| 14 | `jackpot` | Jackpot | 500m | `JackpotAlert` | Stage | Classic / Vegas / Neon |
| 15 | `pixel_storm` | Pixel storm | 550m | `PixelStormAlert` | Stage | 8-bit / 16-bit / Glitch |
| 16 | `treasure` | Treasure chest | 750m | `TreasureChestAlert` | Stage | Classic / Ender / Gold |
| 17 | `golden_shower` | Golden rain | 800m | `GoldenShowerAlert` | Stage | Coins / Bars / Ingots |
| 18 | `money_tornado` | Money tornado | 900m | `MoneyTornadoAlert` | Stage | Bills / Coins / Gems |
| 19 | `nuke` | Nuke | 1b | `NukeAlert` | Stage | Classic / Radioactive / Plasma / Hellfire |
| 20 | `confetti_nuke` | Confetti nuke | 1.2b | `ConfettiNukeAlert` | Stage | Party / Wedding / Carnival |
| 21 | `starfall` | Starfall | 1.5b | `StarfallAlert` | Stage | Night / Dawn / Galaxy |
| 22 | `aurora` | Aurora | 1.8b | `AuroraAlert` | Stage | Green / Purple / Full spectrum |
| 23 | `cherry_blossom` | Cherry blossom | 2b | `CherryBlossomAlert` | Stage | Gentle / Wind / Festival |
| 24 | `meteors` | Meteor shower | 2.5b | `MeteorShowerAlert` | Stage | Classic / Heavy / Binary |
| 25 | `meteor_mini` | Mini meteors | 2.8b | `MeteorMiniAlert` | Stage | Shower / Bolide / Impact |
| 26 | `sandstorm` | Sandstorm | 3b | `SandstormAlert` | Stage | Desert / Dust devil / Haboob |
| 27 | `neon_rain` | Neon rain | 3.5b | `NeonRainAlert` | Stage | City / Blade runner / Acid |
| 28 | `ice_age` | Ice age | 4b | `IceAgeAlert` | Stage | Frost / Shatter / Glacier |
| 29 | `orbital_laser` | Orbital laser | 5b | `OrbitalLaserAlert` | Stage | Classic / Overcharge / Wide |
| 30 | `laser_grid` | Laser grid | 5.5b | `LaserGridAlert` | Stage | Cyan / Magenta / Green |
| 31 | `emp_blast` | EMP blast | 6b | `EmpBlastAlert` | Stage | Pulse / Ring / Grid kill |
| 32 | `tsunami` | Tsunami | 7b | `TsunamiAlert` | Stage | Ocean / Storm / Abyss |
| 33 | `volcano` | Volcano | 8b | `VolcanoAlert` | Stage | Lava / Ash / Eruption |
| 34 | `phoenix` | Phoenix | 9b | `PhoenixAlert` | Stage | Fire / Ash rebirth / Golden |
| 35 | `black_hole` | Black hole | 10b | `BlackHoleAlert` | Stage | Classic / Void / Quasar |
| 36 | `portal_rift` | Portal rift | 12b | `PortalRiftAlert` | Stage | Violet / Void / Prismatic |
| 37 | `glitch` | Glitch | 15b | `GlitchAlert` | Stage | Datamosh / Channel split / Total failure |
| 38 | `blackout` | Blackout | 20b | `BlackoutAlert` | Stage | Grid down / Candle / Emergency |
| 39 | `rainbow_road` | Rainbow road | 25b | `RainbowRoadAlert` | Stage | Neon / Pastel / Retro |
| 40 | `royal_fanfare` | Royal fanfare | 35b | `RoyalFanfareAlert` | Stage | Crimson / Imperial / Silver |
| 41 | `supernova` | Supernova | 50b | `SupernovaAlert` | Stage | Classic / Blue / Gold |
| 42 | `supernova_lite` | Mini supernova | 60b | `SupernovaLiteAlert` | Stage | Blue giant / Red dwarf / Neutron |
| 43 | `god_rays` | God rays | 80b | `GodRaysAlert` | Stage | Dawn / Sanctuary / Judgement |
| 44 | `god_tier` | God tier | 100b | `GodTierAlert` | Stage | Classic / Divine / Void |
| 45 | `lumindev` | Lumindev | 250b / payer-bound | `LumindevAlert` | Stage | Cyclone / Aurora / Void |
| — | `stream` | Stream live | event only | `StreamAlert` | Event | YouTube / Studio / Hype |

**46 effects — 46 custom classes.**

---

## What each stage effect actually does

Durations are the real `duration()` values. `t` is seconds from trigger.

### `sparkle_burst` — 5.6 s · `SparkleBurstAlert`
A shimmer rosette, not a puff. A point of light blooms into 5 petals on a 55 ms stagger,
each petal a soft feathered quad with a white spine. Petals dissolve at 1.25 s into slow
motes that **hang** (negative gravity, drag 2.2) instead of flying out. Three star
satellites orbit at different rates. Audio: ding → sparkle wash → octave ding → two coin
sparkles.

### `cash_cannon` — 6.2 s · `CashCannonAlert`
Actual cannon props in the bottom corners: barrel, breech, muzzle, brass bands, recoil
spring. Each shot spawns 14 tumbling bills with per-bill spin, bounce against an implied
floor at `0.90h`, then get sucked off-screen from 4.3 s. Muzzle flash is a starburst plus
six soft rays. Smoke puffs leak from both barrels throughout. Looks change the fire
pattern: one barrel, alternating, or a 30-shot gatling cadence.

### `party_popper` — 6.4 s · `PartyPopperAlert`
Two handheld poppers bottom-corner and two top-corner, each drawn with a cone body, a mouth
and a pull cord that stretches during the 0.6 s anticipation. Firing sprays 34–52
**streamers** — 9-segment chains of soft line whose tail offsets are driven by two sines, so
they curl rather than point — plus 70 confetti pieces. A wind event at 4.9 s lifts
everything off the top.

### `heart_bomb` — 5.9 s · `HeartBombAlert`
A heart-shaped bomb falls with a sparking fuse, lodges at `0.48h` and its pulse accelerates
from 8.8 Hz to 25 Hz while light cracks spread across it. At 1.8 s it bursts into three
concentric **heart-shaped** shockwave outlines (not rings) plus 48 floating hearts with
drag, then a gold accent every fifth heart.

### `thunder_clap` — 6.6 s · `ThunderClapAlert`
One strike, drawn properly: a 17-segment bolt at `0.085w` amplitude in three passes (22 px
dim blue glow, 11 px mid, 3.2 px white core) with four branches. The screen pre-darkens and
pulses desaturated for the beat before impact, then a ground ring ellipse at the horizon.
Looks add re-strikes at different x-shifts; **God storm** adds a 16-blob scrolling cloud
deck. Rain is real `STREAK` particles for 4.2 s.

### `disco_ball` — 7.2 s · `DiscoBallAlert`
A mirrored ball on a visible chain descends and swings on a damped pendulum, then spins.
The ball is **tiled**: 5 latitude rows × up to 10 longitude tiles, each a rotated mirror
quad tinted by its own palette colour and lit by a travelling highlight. 12 beam triangles
rotate around it, each landing as a floor ellipse patch. **Strobe** adds a 9.5 Hz
white-flash gate and a 12-beam starburst.

### `pixel_storm` — 6.1 s · `PixelStormAlert`
Hard edges only. A 24-cell level bar fills and stamps "LEVEL UP" in blink. The screen then
splits into a 16×9 grid whose cells flip through four hand-plotted 3×3 pixel sprites (coin,
heart, star, arrow) at 14 Hz before settling. Pixel rain falls in 4 px-snapped steps at
12 fps. No `circle` or `ring` call anywhere in the class.

### `golden_shower` — 6.6 s · `GoldenShowerAlert`
A gold seam cracks across the top (`crack[]` offsets) and light pours through nine shafts.
Coins fall in three real depth layers with depth-scaled gravity and spin, **stack** into
drifts at `0.92h` with a per-coin rest timer, and glint when they land. Looks swap the
sprite: coin, bar, or faceted ingot.

### `money_tornado` — 7.2 s · `MoneyTornadoAlert`
A helix, not a column of random points: 170 flakes each sample `height = 0.5+0.5sin(...)`,
`radius = base*(0.4+h*1.5)` and angle `seed*2π + t*speed`, drawn in two passes by sign of
`sin(angle)` so the vortex reads in 2.5D. The funnel walks on two summed sines and throws
bills out of its top. Collapses from 5.0 s.

### `confetti_nuke` — 8.2 s · `ConfettiNukeAlert`
The nuke silhouette with a party payload: a bomb with a streamer trail (seven swaying
segments) detonates at 1.3 s into a translucent stem-and-cap mushroom built from ellipses,
three streamer shockwaves, and confetti rain for five seconds. "PARTY" stamps in one letter
at a time with a glow pop per letter. Draws its own text — no banner.

### `starfall` — 7.6 s · `StarfallAlert`
Night sky first: 170 stars with per-star twinkle phase. One hero star sheds 56 meteors on
`streak()` tails that leave ground glows and spark bursts. Ends on the payer's initial
rendered as a **constellation** — a 5×3 glyph table for A–Z with soft lines between lit
cells. **Galaxy** place the star field as a golden-angle spiral instead of uniform noise.

### `aurora` — 8.6 s · `AuroraAlert`
No particles except a little snow. Five curtains, each 42 quads wide, whose top edge is
`wave(x*0.0035 + phase, 1.7, t*speed) + 0.35*wave(x*0.011 - phase, ...)`, so the folds
never repeat on a visible period. Each curtain has a bright lower hem and a horizontal
alpha falloff. **Full spectrum** rotates hue per curtain *and* along each curtain.

### `cherry_blossom` — 8.2 s · `CherryBlossomAlert`
Wind is the simulation. 260 petals in three depth layers integrate a gusting wind field
(slow sine plus a spike when `sin > 0.85`), flutter on `cos(rot*1.7)`, settle on a ground
line for up to 1.8 s, and blow off again when the gust exceeds 140. A recursive 4-deep
branch enters from the top-left. **Festival** hangs five swaying lanterns behind the petals.

### `meteor_mini` — 4.8 s · `MeteorMiniAlert`
The cheap little brother, deliberately under five seconds: nine scheduled rocks on short
arcs, a bolide at 2.0 s that splits into eight fragments, and per-impact craters that glow
and die over 1.6 s. Reds the sky from the top down.

### `sandstorm` — 7.2 s · `SandstormAlert`
Visibility is the mechanic. A wall of four stacked billow bands (each 17 ellipses scrolling
at a different rate) advances from the right, exits left at 5.1 s. Haze is two full-screen
washes at different alphas. Sand rips across at three depths as `STREAK` particles with
depth-scaled speed. Four tumbleweeds roll with spoke lines. **Dust devil** adds a 30-point
helix funnel inside the haze.

### `neon_rain` — 7.2 s · `NeonRainAlert`
A built skyline: 13 buildings with generated window grids, a per-window flicker hash, and
**mirrored reflections** below the horizon line. Wet plane has seven breathing highlight
lines, ripples spawn and expand, and rain drops draw both themselves and a dim mirror
image below the horizon.

### `ice_age` — 7.6 s · `IceAgeAlert`
Frost **ferns** creep in from all four edges via recursive branching (3 deep, 3 children),
then 42 ice shards grow upward from the bottom. The name is etched in frost with a glow.
At 5.0 s a 26-ray crack web fires and the shards fall, spinning and accelerating. **Shatter**
moves the break to 3.6 s; **Glacier** grows much taller slabs.

### `laser_grid` — 6.6 s · `LaserGridAlert`
Heist vault. HUD brackets draw themselves in the four corners, a scan line sweeps, a
`RANGE / LOCK` readout ticks up. A perspective grid then converges on a vanishing point
and a diamond locks around it with a 12 Hz pulse. Seven timed white cuts slice the frame,
and the whole grid retracts from 5.1 s.

### `emp_blast` — 6.1 s · `EmpBlastAlert`
A charge ring gathers, twenty-four spokes rotate in, then the pulse fires: hard white ring
past the frame edges, three harmonics, and the frame glitches — 14 displaced bands with
channel-coloured seams, 26 duplicated block clusters, and inversion frames. A dead-pixel
grid flickers back on at 48 px, then a boot sweep prints `SYSTEMS ONLINE`.

### `tsunami` — 7.2 s · `TsunamiAlert`
The horizon tilts (pose push/rotate/pop). A 64-segment crest profile is a gaussian riding a
sine swell; the body fills to the floor, the lip gets a bright line and foam circles placed
deterministically from `wave()` so they do not flicker. At 3.2 s the camera goes underwater:
nine caustic lines and rising bubbles. Debris and foam settle at the end.

### `volcano` — 8.2 s · `VolcanoAlert`
Ground-up. A mountain silhouette rises from an 18-value ridge table, the vent glows, then a
two-layer column fires: 24 ash ellipses outside a bright lava core. 40 ballistic lava bombs
with smoke trails, a pressure ring on the ground, glowing ground veins, and a falling ash
layer for six seconds.

### `phoenix` — 9.2 s · `PhoenixAlert`
Two acts with a real dead pause: a guttering flame dies at 1.5 s, the frame holds nearly
empty, then a spark at 2.2 s reignites and a bird forms from eighteen feather lines fanning
from a body. Two wing beats (every 0.65 s) each throw 26 embers, then the bird climbs off
the top.

### `portal_rift` — 6.9 s · `PortalRiftAlert`
An **eye-shaped** rift: two lid curves meeting at a seam with 15 jagged values, a chord
stack for the dark interior, an ellipse pupil with a glimpsed far-side horizon line. Seven
concentric strata compress toward it (inverse-distance warp), 90 motes spiral in, a
silhouette steps through the pupil, then the lid snaps shut and one ring escapes.

### `glitch` — 6.4 s · `GlitchAlert`
The frame is the subject. 18 bands of the plate detach and slide with colour-channel seam
smears, 26 block clusters duplicate off-axis, and the name is drawn three times in RGB
channel colours at up to 26 px split before snapping clean. Runs at a 12 fps hold so it
jitters rather than slides. At 3.1 s the whole image collapses to a scan line and re-forms
at 3.8 s. CRT scanlines and a roll bar run throughout.

### `blackout` — 6.2 s · `BlackoutAlert`
The anti-flashbang. A hard cut at 0.25 s to absolute black — no fade — then a six-segment
arc flickers once, a spotlight cone snaps on and only the name is lit inside it, with dust
motes visible only within the cone's width/depth falloff. Restore at 3.9 s is deliberately
over-exposed (0.92 alpha white) and colours bleed back in from the edges. **Emergency**
adds a rotating red beacon sweep.

### `rainbow_road` — 6.8 s · `RainbowRoadAlert`
One-point perspective. 22 depth rows (non-linear spacing) × 12 segments, hue rotating along
the road's length, with a white chevron per row and bright side rails. The vanishing point
drifts on a slow figure-eight and rises as the camera climbs, and the road banks into a
shallow arc with lifted rail duplicates.

### `royal_fanfare` — 8.0 s · `RoyalFanfareAlert`
Theatre. Two velvet curtains with nine folds each part on a brass rail with finials, a
crest builds **stroke by stroke** (hex shield, progressive ring arc, chevrons, laurel
ellipse pairs, crown with three points), two banner columns unfurl, and gold leaf drifts
down at its own slow rate and twinkles.

### `supernova_lite` — 6.5 s · `SupernovaLiteAlert`
Inward, not outward. 90 shell sparks travel **toward** the core with staggered delays,
leaving trails that point back outward, while three wobble rings contract. The core
shrinks and the screen darkens as it feeds. The implosion holds for two frames of black
with a single white point, then a ring blast plus a 60-blob nebula that cools and
disperses.

### `god_rays` — 8.4 s · `GodRaysAlert`
Twelve volumetric shafts fan from a crown at `0.14h`, each a trapezoid widening to 4.5× at
the floor, laid down one after another on a stagger, with a hot core line and a floor pool.
110 dust motes are only drawn where they fall inside the cone (`1 - lateral/halfWidth`).
The cloud deck parts around the crown, and a three-ring halo with orbiting stars rotates
around the name. Draws its own text.

### `lumindev` — 15.2 s · `LumindevAlert` (payer-bound)
Fires for **any** payment from Lumindev (`lumindev`, `lumin`, `lumindevmc`, case- and
format-insensitive) at any amount, and sits above `god_tier` at 250b on the ladder.

1. **0.00–1.05** hard cut to near-black; a seam of light opens down the middle; aurora
   ribbons start rolling behind it.
2. **1.05–2.25** the seam tears into a rift; the name arrives as a chromatic-split glitch
   with "THE DEV DROPPED BY" beneath it.
3. **2.25** impact — white flash, expanding shockwave, 0.9-strength shake.
4. **2.25–4.40** the payer descends in a column of light and dances.
5. **4.40–11.20** full showcase: god-ray fan, halo, orbiting helix of coins and embers,
   drifting confetti, and an odometer-style amount count-up.
6. **11.20–15.20** everything is pulled back into the seam, the screen whites out, and
   `cyclone skies · lumindev` is left behind.

Looks: **Cyclone** (violet/blue/cyan), **Aurora** (teal/mint/gold), **Void**
(magenta/ice/white). 17 scripted sound cues.

### `stream` — 9.4 s · `StreamAlert` (event-bound)

The channel goes live. This one is not a payment effect at all: it fires when chat
announces the stream, or when `/payalerts stream [title]` is run by hand. `Tier.STREAM`
has `Kind.EVENT` and a threshold of `0`, and `AlertManager.pick` skips every kind other
than `SIDE`/`STAGE`, so it can never win the amount ladder.

Every beat is broadcast hardware, not particles:

1. **0.00–1.05** standby: near-black frame, SMPTE test bars along the top under 7.5 Hz
   scanlines, a slowly blinking red tally dot over `STANDBY`, and a sliding laser hum.
2. **1.05** hard cut to air — one frame of the palette red over the whole screen, then
   the frame settles into the channel body.
3. **1.05–2.25** an eight-blade lens aperture rotates open (`inner = 0.62u·(1-open)²`)
   with a hinge line on each blade and an iris glow leaking through the gap.
4. **2.25–3.05** identity block: the channel name at up to 4.6× fitted scale, the
   `@lumindevdsmp` handle in accent, `youtube.com/@lumindevdsmp` beneath it.
5. **2.40–7.20** lower third slides in from the left with a live-timecode rule growing
   along its bottom edge. The subtitle is the stream title, or `is live now` when none
   was given.
6. **3.05–6.00** six chat chips stack down the right edge, each with its own hue, avatar
   dot, name bar and message bar, staggered 0.42 s apart with a coin pickup each.
7. **3.05+** a broadcast progress line runs along the bottom with a lit head.
8. **5.45–7.20** the subscribe button: bell, label, press dip to 0.93 scale at 6.05,
   an expanding ripple, and a bell star flash.
9. **7.55–9.40** red wipe left to right with a lit leading edge, then the end card:
   `LIVE NOW`, `channel @lumindevdsmp`, `youtube.com/@lumindevdsmp`.

A viewer counter rides from 0 to ~1.9K with `outCubic` between 1.15 and 2.75 s.

Looks: **YouTube** (broadcast red), **Studio** (cyan on near-black), **Hype** (violet
and magenta). 22 scripted sound cues.

---

## Adding another effect

1. New class in `com.paymentalerts.alert` extending `StageBase` (or implementing
   `StageAlert`/`SideAlert` for something simpler).
2. `static final String[] VARIATIONS` on the class.
3. A `case` in `EffectFactory.createStage` / `createSide`.
4. A `case` in `Tier.variations()`.
5. If it needs new audio, a key in `assets/paymentalerts/sounds.json` and a constant in
   `AlertSounds`.

Everything else — routing, queueing, theme binding, config thresholds, the settings list —
is generic over `Tier`.

For an effect that is **not** a payment amount (a stream, a milestone, a scheduled
event), use `Kind.EVENT` instead of `Kind.STAGE`. `AlertManager.pick` filters those out
of the ladder, so the tier is reachable only through an explicit call — add one public
method next to `AlertManager.stream(channel, title)` and give it a `Queued` note if it
needs a string argument. `Kind.EVENT` alerts are entered through the same `STAGE_QUEUE`
and share the same single-stage slot.
