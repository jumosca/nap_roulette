package com.naproulette.domain.usecase

import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class GetNapVerdict @Inject constructor() {

    operator fun invoke(duration: Duration): String {
        val minutes = duration.inWholeMinutes
        return when {
            minutes < 5 -> "Did you even close your eyes? That was a blink, not a nap."
            minutes < 15 -> "Micro nap unlocked! Your brain got a quick defrag."
            minutes < 25 -> "NASA-approved power nap! You're basically an astronaut now."
            minutes < 40 -> "Solid nap. You're operating at peak human laziness."
            minutes < 60 -> "That's a proper siesta! Your Mediterranean ancestors are proud."
            minutes < 90 -> "You entered the sleep matrix. Did you see the code?"
            minutes < 120 -> "Full sleep cycle complete. You're basically a new person."
            else -> "That wasn't a nap, that was a lifestyle choice. Welcome back to reality."
        }
    }
}
