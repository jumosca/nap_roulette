package com.naproulette.data.repository

import com.naproulette.data.local.NapSessionDao
import com.naproulette.domain.model.NapSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NapRepository @Inject constructor(
    private val dao: NapSessionDao
) {
    fun getAllSessions(): Flow<List<NapSession>> = dao.getAllSessions()

    fun getRecentSessions(limit: Int = 10): Flow<List<NapSession>> = dao.getRecentSessions(limit)

    suspend fun saveSession(session: NapSession): Long = dao.insert(session)
}
