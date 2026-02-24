package com.naproulette

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NapRouletteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val timerChannel = NotificationChannel(
            TIMER_CHANNEL_ID,
            "Nap Timer",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows countdown while nap is in progress"
            setShowBadge(false)
        }

        val alarmChannel = NotificationChannel(
            ALARM_CHANNEL_ID,
            "Nap Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alarm when nap is over"
            setSound(null, null) // AlarmSoundPlayer handles audio
            enableVibration(false) // AlarmSoundPlayer handles vibration
            setBypassDnd(true)
        }

        notificationManager.createNotificationChannels(listOf(timerChannel, alarmChannel))
    }

    companion object {
        const val TIMER_CHANNEL_ID = "nap_timer"
        const val ALARM_CHANNEL_ID = "nap_alarm"
    }
}
