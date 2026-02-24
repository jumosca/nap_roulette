package com.naproulette.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(tableName = "nap_sessions")
data class NapSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMillis: Long,
    val plannedDurationMillis: Long,
    val actualDurationMillis: Long,
    val wasCompleted: Boolean,
    val wasSnoozed: Boolean = false,
    val presetName: String? = null
) {
    val plannedDuration: Duration get() = Duration.parse("${plannedDurationMillis}ms")
    val actualDuration: Duration get() = Duration.parse("${actualDurationMillis}ms")
}
