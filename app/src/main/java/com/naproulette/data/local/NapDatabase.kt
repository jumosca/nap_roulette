package com.naproulette.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.naproulette.domain.model.NapSession

@Database(entities = [NapSession::class], version = 1, exportSchema = true)
abstract class NapDatabase : RoomDatabase() {
    abstract fun napSessionDao(): NapSessionDao
}
