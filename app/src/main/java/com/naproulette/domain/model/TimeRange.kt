package com.naproulette.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class TimeRange(
    val min: Duration = 10.minutes,
    val max: Duration = 120.minutes
) {
    init {
        require(min >= 1.minutes) { "Minimum must be at least 1 minute" }
        require(max > min) { "Maximum must be greater than minimum" }
    }
}
