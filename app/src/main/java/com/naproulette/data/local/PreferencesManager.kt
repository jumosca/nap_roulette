package com.naproulette.data.local

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "nap_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.dataStore

    private object Keys {
        val MIN_MINUTES = floatPreferencesKey("min_minutes")
        val MAX_MINUTES = floatPreferencesKey("max_minutes")
        val ALARM_SOUND_TYPE = stringPreferencesKey("alarm_sound_type")
        val ALARM_SOUND_ID = stringPreferencesKey("alarm_sound_id")
        val CUSTOM_SOUND_URI = stringPreferencesKey("custom_sound_uri")
        val CUSTOM_SOUND_NAME = stringPreferencesKey("custom_sound_name")
    }

    val minMinutes: Flow<Float> = store.data.map { it[Keys.MIN_MINUTES] ?: 10f }
    val maxMinutes: Flow<Float> = store.data.map { it[Keys.MAX_MINUTES] ?: 120f }
    val alarmSoundType: Flow<String> = store.data.map { it[Keys.ALARM_SOUND_TYPE] ?: "bundled" }
    val alarmSoundId: Flow<String> = store.data.map { it[Keys.ALARM_SOUND_ID] ?: "gentle_chime" }
    val customSoundUri: Flow<String?> = store.data.map { it[Keys.CUSTOM_SOUND_URI] }
    val customSoundName: Flow<String?> = store.data.map { it[Keys.CUSTOM_SOUND_NAME] }

    suspend fun setRange(minMinutes: Float, maxMinutes: Float) {
        store.edit {
            it[Keys.MIN_MINUTES] = minMinutes
            it[Keys.MAX_MINUTES] = maxMinutes
        }
    }

    suspend fun setAlarmSound(type: String, id: String) {
        store.edit {
            it[Keys.ALARM_SOUND_TYPE] = type
            it[Keys.ALARM_SOUND_ID] = id
        }
    }

    suspend fun setCustomSound(uri: Uri, name: String) {
        store.edit {
            it[Keys.ALARM_SOUND_TYPE] = "custom"
            it[Keys.CUSTOM_SOUND_URI] = uri.toString()
            it[Keys.CUSTOM_SOUND_NAME] = name
        }
    }
}
