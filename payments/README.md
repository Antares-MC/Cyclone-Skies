# Donut SMP Payment Addon (paymentalerts)

Reconstructed / decompiled source of **DonutSMPPaymentAddon-26.2 (2).jar**.

## Original metadata
| Field | Value |
|-------|-------|
| Mod ID | `paymentalerts` |
| Version | `2.7.0+26.2` |
| Environment | client-only |
| Minecraft | `~26.2` |
| Fabric Loader | `>=0.20.1` |
| Java | `>=21` (build environment typically needs **JDK 25+** for modern Loom) |
| Fabric API | required |

## Features
Client-side alerts when someone pays you in chat. Multiple visual & audio tiers ranging from a simple 1M coin pop up to crowned 100B+ "god tier" effects. Approximately 15 distinct effects with variations. Only the local player sees and hears them.

Keybinds (defaults in the combined build; K moved to H):
- **H** – toggle alerts
- **O** – open settings
- **J** – show payment stats

## Building

**Requirements**
- JDK 25 or newer (Fabric Loom 1.19+ for Minecraft 26.x requires it)
- Internet access (downloads Minecraft, mappings, Fabric API, etc.)

```bash
./gradlew build
```

The output jar is written to `build/libs/paymentalerts-2.7.0+26.2.jar` (exact name depends on `archives_base_name` + version).

You can also import the project into IntelliJ IDEA or another IDE that supports Gradle + Fabric Loom.

## Project layout
```
src/main/java/com/paymentalerts/
├── PaymentAlertsClient.java   # Client entrypoint
├── alert/                     # All visual alert implementations + manager
├── command/                   # /payalerts client command
├── config/                    # Config loading / tiers
├── fx/                        # Particles, fireworks, screen shake, drawing helpers
├── mixin/                     # Hud, GameRenderer, SplashManager, accessor
├── payment/                   # Payment parsing & amount formatting
├── screen/                    # Settings, stats, leaderboard, effects list GUIs
├── skin/                      # Player-skin dolls / PIP renderers
├── sound/                     # Alert sound cues
└── stats/                     # Session & lifetime payment statistics

src/main/resources/
├── fabric.mod.json
├── paymentalerts.mixins.json
└── assets/paymentalerts/
    ├── lang/en_us.json
    └── sounds.json            # Maps logical sounds → vanilla events
```

## Notes on this reconstruction
- Source was produced by jadx decompilation of the distributed JAR.
- Variable names, control-flow structure, and some local types may differ slightly from the original (normal for decompilers).
- All resources (lang, sounds.json, mixin config) were taken directly from the JAR.
- No original source repository or Gradle files were present; the Loom project structure was created so the mod can be rebuilt and modified.
- The mod is client-side only and registers a `PictureInPictureRenderer` for the skin-doll system used by some alerts.

If you encounter compilation errors after a Minecraft/Fabric API update, the most likely places needing attention are the mixins and any direct references to renamed GuiGraphicsExtractor / render-state APIs.


## Credits

| Role | Name |
|------|------|
| **Original author** | **cobha** |
| **Developers** | **cyclonelwk**, **Lumina** |

Originally created by **cobha**. Continued development by **cyclonelwk** and **Lumina**.
