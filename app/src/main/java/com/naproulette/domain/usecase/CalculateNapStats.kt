package com.naproulette.domain.usecase

import com.naproulette.domain.model.NapSession
import com.naproulette.domain.model.NapStats
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class CalculateNapStats @Inject constructor() {

    operator fun invoke(sessions: List<NapSession>): NapStats {
        if (sessions.isEmpty()) return NapStats()

        val completed = sessions.filter { it.wasCompleted }
        val durations = completed.map { it.actualDurationMillis }

        // Calculate streak: consecutive completed naps from most recent
        var streak = 0
        for (session in sessions.sortedByDescending { it.startTimeMillis }) {
            if (session.wasCompleted) streak++ else break
        }

        return NapStats(
            totalNaps = sessions.size,
            completedNaps = completed.size,
            averageDuration = if (durations.isNotEmpty()) {
                (durations.average().toLong()).milliseconds
            } else {
                0.milliseconds
            },
            totalNapTime = durations.sum().milliseconds,
            longestNap = (durations.maxOrNull() ?: 0).milliseconds,
            shortestNap = (durations.minOrNull() ?: 0).milliseconds,
            currentStreak = streak
        )
    }
}
