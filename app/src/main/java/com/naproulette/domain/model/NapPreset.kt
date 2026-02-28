package com.naproulette.domain.model

import kotlin.time.Duration.Companion.minutes

enum class NapPreset(
    val displayName: String,
    val description: String,
    val range: TimeRange,
    val suit: String,
    val isRedSuit: Boolean
) {
    POWER_NAP(
        displayName = "Power Nap",
        description = "Quick recharge",
        range = TimeRange(min = 5.minutes, max = 15.minutes),
        suit = "♦",
        isRedSuit = true
    ),
    COFFEE_NAP(
        displayName = "Coffee Nap",
        description = "Drink coffee, then nap",
        range = TimeRange(min = 15.minutes, max = 25.minutes),
        suit = "♠",
        isRedSuit = false
    ),
    SIESTA(
        displayName = "Siesta",
        description = "Afternoon rest",
        range = TimeRange(min = 30.minutes, max = 60.minutes),
        suit = "♥",
        isRedSuit = true
    ),
    FULL_CYCLE(
        displayName = "Full Cycle",
        description = "Complete sleep cycle",
        range = TimeRange(min = 80.minutes, max = 100.minutes),
        suit = "♣",
        isRedSuit = false
    )
}
