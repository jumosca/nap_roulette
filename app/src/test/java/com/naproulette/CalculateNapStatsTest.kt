package com.naproulette

import com.naproulette.domain.model.NapSession
import com.naproulette.domain.usecase.CalculateNapStats
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateNapStatsTest {

    private val useCase = CalculateNapStats()

    @Test
    fun `empty list returns default stats`() {
        val stats = useCase(emptyList())
        assertEquals(0, stats.totalNaps)
        assertEquals(0, stats.completedNaps)
        assertEquals(0, stats.currentStreak)
    }

    @Test
    fun `calculates stats from sessions`() {
        val sessions = listOf(
            NapSession(
                id = 1,
                startTimeMillis = 1000,
                plannedDurationMillis = 30 * 60_000L,
                actualDurationMillis = 30 * 60_000L,
                wasCompleted = true
            ),
            NapSession(
                id = 2,
                startTimeMillis = 2000,
                plannedDurationMillis = 20 * 60_000L,
                actualDurationMillis = 20 * 60_000L,
                wasCompleted = true
            ),
            NapSession(
                id = 3,
                startTimeMillis = 3000,
                plannedDurationMillis = 60 * 60_000L,
                actualDurationMillis = 10 * 60_000L,
                wasCompleted = false
            )
        )

        val stats = useCase(sessions)
        assertEquals(3, stats.totalNaps)
        assertEquals(2, stats.completedNaps)
        assertEquals(25 * 60_000L, stats.averageDuration.inWholeMilliseconds)
        assertEquals(30 * 60_000L, stats.longestNap.inWholeMilliseconds)
        assertEquals(20 * 60_000L, stats.shortestNap.inWholeMilliseconds)
    }

    @Test
    fun `streak counts consecutive completed from most recent`() {
        val sessions = listOf(
            NapSession(1, 1000, 0, 0, wasCompleted = true),
            NapSession(2, 2000, 0, 0, wasCompleted = false),
            NapSession(3, 3000, 0, 0, wasCompleted = true),
            NapSession(4, 4000, 0, 0, wasCompleted = true)
        )
        val stats = useCase(sessions)
        assertEquals(2, stats.currentStreak) // IDs 3,4 are consecutive from recent
    }
}
