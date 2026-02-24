package com.naproulette

import com.naproulette.domain.model.TimeRange
import com.naproulette.domain.usecase.GenerateRandomDuration
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

class GenerateRandomDurationTest {

    private val useCase = GenerateRandomDuration()

    @Test
    fun `generated duration is within range`() {
        val range = TimeRange(min = 10.minutes, max = 60.minutes)
        repeat(100) {
            val duration = useCase(range, Random(it))
            assertTrue(
                "Duration $duration should be >= ${range.min}",
                duration >= range.min
            )
            assertTrue(
                "Duration $duration should be <= ${range.max}",
                duration <= range.max
            )
        }
    }

    @Test
    fun `generated duration is rounded to whole minutes`() {
        val range = TimeRange(min = 5.minutes, max = 120.minutes)
        repeat(50) {
            val duration = useCase(range, Random(it))
            assertTrue(
                "Duration should be in whole minutes, got $duration",
                duration.inWholeMilliseconds % 60_000 == 0L
            )
        }
    }

    @Test
    fun `narrow range produces duration within bounds`() {
        val range = TimeRange(min = 15.minutes, max = 20.minutes)
        repeat(50) {
            val duration = useCase(range, Random(it))
            assertTrue(duration >= 15.minutes)
            assertTrue(duration <= 20.minutes)
        }
    }
}
