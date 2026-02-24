package com.naproulette.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.naproulette.domain.model.NapSession
import kotlinx.coroutines.flow.Flow

@Dao
interface NapSessionDao {

    @Insert
    suspend fun insert(session: NapSession): Long

    @Query("SELECT * FROM nap_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<NapSession>>

    @Query("SELECT * FROM nap_sessions ORDER BY startTimeMillis DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<NapSession>>

    @Query("SELECT COUNT(*) FROM nap_sessions")
    suspend fun getSessionCount(): Int
}
