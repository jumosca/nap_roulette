package com.naproulette.domain.model

import kotlin.time.Duration

data class NapStats(
    val totalNaps: Int = 0,
    val completedNaps: Int = 0,
    val averageDuration: Duration = Duration.ZERO,
    val totalNapTime: Duration = Duration.ZERO,
    val longestNap: Duration = Duration.ZERO,
    val shortestNap: Duration = Duration.ZERO,
    val currentStreak: Int = 0
)
