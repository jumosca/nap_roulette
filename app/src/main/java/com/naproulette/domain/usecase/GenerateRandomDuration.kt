package com.naproulette.domain.usecase

import com.naproulette.domain.model.TimeRange
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class GenerateRandomDuration @Inject constructor() {

    operator fun invoke(range: TimeRange, random: Random = Random): Duration {
        val minMillis = range.min.inWholeMilliseconds
        val maxMillis = range.max.inWholeMilliseconds
        val randomMillis = random.nextLong(minMillis, maxMillis + 1)
        // Round to nearest minute for clean display
        val roundedMinutes = (randomMillis / 60_000)
        return (roundedMinutes * 60_000).milliseconds
    }
}
