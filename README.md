# Nap Roulette

A casino-themed Android nap timer. Set a time range, spin the roulette wheel, and let fate decide your nap length. 

<img src="screenshot4.jpeg" width=15% height=15%> <img src="screenshot5.jpeg" width=15% height=15%> <img src="screenshot3.jpeg" width=15% height=15%> <img src="screenshot2.jpeg" width=15% height=15%> <img src="screenshot1.jpeg" width=15% height=15%>

---

## Features

- **Spin to nap** — Set a min/max range and spin for a randomized countdown
- **Preset profiles** — Power Nap, Coffee Nap, Full Cycle, Siesta (displayed as a playing card fan)
- **Background reliability** — AlarmManager + ForegroundService keeps the timer alive
- **Full-screen alarm** — Wakes the screen even from the lock screen, with dismiss and snooze
- **9 bundled alarm sounds** — Casino and classic categories with in-app preview
- **Custom sound support** — Pick any audio file from your device
- **Nap stats dashboard** — Total naps, completion rate, average duration, longest nap (slot machine style display)
- **Verdict card** — Post-nap verdict shown after each completed session
- **Boot survival** — Scheduled alarm persists across device restarts

## Aesthetic

Early 20th century lithography meets mid-century French casino. Custom "Grand Casino Demo" display font, a green baize header, vintage paper tones, and a grain overlay texture throughout.

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Architecture:** MVVM with a single `NapViewModel`
- **DI:** Hilt
- **Persistence:** Room (nap session history) + DataStore (preferences)
- **Background:** `AlarmManager` + `TimerService` (foreground service)
- **Min SDK:** 26 (Android 8.0 Oreo)

---

## Project Structure

```
app/src/main/java/com/naproulette/
├── MainActivity.kt
├── viewmodel/
│   └── NapViewModel.kt
├── ui/
│   ├── screen/          # NapScreen, SoundPicker, RangeSelector, TimerDisplay, AlarmFiringOverlay, AlarmActivity
│   ├── components/      # SpinButton, NapProgressArc, PresetCards, GrainOverlay
│   └── theme/           # Color, Theme, Type
├── service/             # TimerService, AlarmReceiver, AlarmSoundPlayer, AlarmDismissReceiver, BootReceiver
├── domain/
│   ├── model/           # AlarmSound, NapPreset, NapSession, NapStats, TimeRange, TimerState
│   └── usecase/         # GenerateRandomDuration, CalculateNapStats, GetNapVerdict
└── data/
    ├── local/           # NapDatabase, NapSessionDao, PreferencesManager
    └── repository/      # NapRepository
```

---

## Timer State Machine

```
IDLE → SPINNING (2s animation) → COUNTING_DOWN → ALARM_FIRING → IDLE
```

---

## Status

Available on [Android Play Store](https://play.google.com/store/apps/details?id=com.naproulette.app) .

---

## Alarm Sounds (bundled)

| Category | Sounds |
|---|---|
| Casino | casino_sounds, coins, roulette, slot_machine |
| Classic | birds, classic_alarm, coffee_shop, gentle_chime, ocean_waves |

---

## License

MIT
