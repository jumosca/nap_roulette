# Nap Roulette - Android App Implementation Plan

## Context

Build a new Android app called "Nap Roulette" — a fun, single-screen nap timer where the user sets a time range (e.g. 10min–2hrs), hits a button, and gets a random countdown. When it hits zero, an alarm plays. The app needs to work reliably in the background (screen off, app minimized) and support multiple alarm sound sources.

## Technology: Kotlin + Jetpack Compose (Native Android)

- **Why native**: Background alarms (`AlarmManager`, `ForegroundService`) are first-class Android APIs. Cross-platform frameworks (Flutter, RN) would need brittle native bridges for zero benefit on a single-platform, single-screen app.
- **Why Compose**: Modern declarative UI, great animation support for countdown display, simpler than XML for a single-screen app.
- **Spotify SDK**: The official Android App Remote SDK is native Kotlin/Java — no wrappers needed.
- **YouTube**: Skipped — no official playback SDK, unreliable as an alarm source.
- **Min SDK 26** (Android 8.0, 95%+ device coverage)

## Visual Style: Early 20th Century Lithography + Mid-Century French Casino

The app's visual identity draws from **vintage lithographic poster art** and **1950s-60s French casino/gaming aesthetics**. Think Toulouse-Lautrec meets Monte Carlo.

> **Note**: Detailed style guide (color palette, typography, specific design tokens) to be provided separately by the user. The theme system will be built to accommodate these choices.

Key style directions:
- Rich, warm color palette (deep reds, golds, creams, dark greens — exact values TBD from style guide)
- Typography inspired by vintage poster lettering / art deco casino signage
- Textured, slightly aged visual quality — not flat/modern material design
- Roulette/casino motifs for the spin button and UI accents
- The countdown display styled like vintage casino number boards or lithographic numerals

## Project Structure

```
nap_roulette/
├── app/src/main/java/com/naproulette/
│   ├── NapRouletteApp.kt           # Application class (Hilt entry)
│   ├── MainActivity.kt              # Single activity, hosts Compose
│   ├── ui/
│   │   ├── theme/                   # Custom theme (lithography/casino style)
│   │   │   ├── Theme.kt
│   │   │   ├── Color.kt
│   │   │   └── Type.kt
│   │   ├── screen/
│   │   │   ├── NapScreen.kt        # Main composable (timer + controls)
│   │   │   ├── TimerDisplay.kt     # Large countdown clock
│   │   │   ├── RangeSelector.kt    # Min/max time picker
│   │   │   ├── SoundPicker.kt      # Alarm sound selection
│   │   │   └── AlarmFiringOverlay.kt  # Full-screen alarm dismiss
│   │   └── components/
│   │       ├── SpinButton.kt       # Animated roulette button
│   │       └── NapProgressArc.kt   # Circular progress ring
│   ├── viewmodel/
│   │   └── NapViewModel.kt         # UI state + timer orchestration
│   ├── domain/
│   │   ├── model/                   # NapTimer, TimeRange, AlarmSound, NapSession
│   │   └── usecase/                 # GenerateRandomDuration, CalculateNapStats
│   ├── data/
│   │   ├── local/                   # Room DB, DataStore preferences
│   │   └── repository/             # NapRepository
│   ├── service/
│   │   ├── TimerService.kt         # Foreground service for countdown
│   │   ├── AlarmReceiver.kt        # BroadcastReceiver for alarm trigger
│   │   └── AlarmSoundPlayer.kt     # Unified audio manager
│   └── di/
│       └── AppModule.kt            # Hilt DI module
├── app/src/main/res/raw/           # 5 bundled alarm sounds
├── gradle/libs.versions.toml       # Version catalog
└── build.gradle.kts
```

## Architecture: How the Timer Works

Two-layer approach (same pattern as the stock Android Clock app):

1. **`AlarmManager.setExactAndAllowWhileIdle()`** — schedules the exact alarm time. Survives Doze mode and process death. This is the source of truth.
2. **`ForegroundService` (TimerService)** — shows persistent notification with live countdown. Provides `StateFlow` to the UI. Even if the service is killed, AlarmManager still fires.

**Timer states:** `IDLE → SPINNING (animation) → COUNTING_DOWN → ALARM_FIRING → IDLE`

When alarm fires: full-screen intent wakes the screen (even on lock screen), sound plays via `MediaPlayer` with `USAGE_ALARM` audio attributes (plays through DND), device vibrates, user gets Dismiss/Snooze buttons.

## Key Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose + Material3 | UI framework |
| Hilt | Dependency injection |
| Room | Nap session history DB |
| DataStore | User preferences |
| Spotify App Remote SDK | Spotify playback control |
| MockK + Turbine | Testing |

## Sound Sources

1. **Bundled presets** (5 sounds in `res/raw/`): gentle chime, birds, ocean waves, coffee shop, classic alarm
2. **Custom upload**: User picks audio via `ACTION_OPEN_DOCUMENT` with persistable URI permission
3. **Spotify** (Phase 4): Auth via Spotify Auth SDK, playback via App Remote SDK. Fallback to bundled sound if Spotify unavailable.

## Features (included in initial release)

- Slot-machine spin animation when generating random time (with haptics)
- Preset profiles: "Power Nap" (10-20m), "Coffee Nap" (15-25m), "Full Cycle" (80-100m), "Siesta" (30-60m)
- Vintage lithography/casino themed UI
- Nap statistics dashboard (total naps, avg duration, streaks)

**Future (not in initial release):**
- Achievements/badges, shake to dismiss, gradual volume ramp
- Sleep sounds during nap, home screen widget, Spotify playlist shuffle
- Clear nap history button
- social media share button for nap stats

## Implementation Phases

### Phase 1 — Core Timer (MVP)
- Generate project with Android Studio Empty Compose Activity template
- Set up Hilt DI
- Build `TimerDisplay`, `RangeSelector`, `SpinButton` composables
- Implement random duration generation + countdown logic in `NapViewModel`
- Play bundled alarm sound when timer hits zero
- Basic stop/cancel — **works in foreground only**

### Phase 2 — Background & Alarm
- `TimerService` foreground service with persistent notification
- `AlarmManager` scheduling + `AlarmReceiver`
- `AlarmFiringOverlay` with full-screen intent (wakes screen, shows on lock screen)
- All permissions (exact alarm, notifications, vibrate, full-screen intent)
- Reboot handling (`BOOT_COMPLETED` receiver)
- Dismiss + snooze functionality

### Phase 3 — Sound & Polish
- Bundle 5 preset sounds, build `SoundPicker` with preview
- Custom sound upload with persistable URI
- Spin animation + digit transition animations
- Circular progress arc
- Vintage lithography/casino theme implementation (based on style guide)
- Haptic feedback

### Phase 4 — Spotify & Persistence
- Spotify Auth + App Remote SDK integration
- Spotify track/playlist picker + fallback handling
- Room database for nap session history
- Statistics dashboard
- Nap presets

### Phase 5 — Extras & Release
- Achievements, shake-to-dismiss
- Home screen widget (Glance)
- Accessibility audit, performance profiling
- Play Store listing prep, ProGuard, beta testing

## Verification

- **Unit tests**: Random duration generation (always within range), ViewModel state transitions (via Turbine), stats calculation
- **Compose UI tests**: Range slider updates, spin button triggers countdown, stop button works
- **Manual integration tests**: Background timer (start, press home, wait for alarm), process death (force-kill app, verify AlarmManager fires), reboot survival, DND behavior, Spotify auth + playback + fallback
- **Device testing**: Multiple API levels (26, 31, 33, 34+), Samsung/Xiaomi battery optimization behavior
