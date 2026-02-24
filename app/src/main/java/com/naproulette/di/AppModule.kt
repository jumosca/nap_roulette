package com.naproulette.di

import android.content.Context
import androidx.room.Room
import com.naproulette.data.local.NapDatabase
import com.naproulette.data.local.NapSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NapDatabase {
        return Room.databaseBuilder(
            context,
            NapDatabase::class.java,
            "nap_roulette.db"
        ).build()
    }

    @Provides
    fun provideNapSessionDao(db: NapDatabase): NapSessionDao = db.napSessionDao()
}
