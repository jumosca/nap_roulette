package com.naproulette.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm fired!")
        // Forward to TimerService — it handles sound, state, and notification.
        // Starting a foreground service from AlarmManager is always allowed (no background
        // activity launch restrictions). Sound extras are forwarded in case the process was
        // killed since the timer started.
        val serviceIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_ALARM_FIRED
            intent.getStringExtra(TimerService.EXTRA_SOUND_TYPE)?.let {
                putExtra(TimerService.EXTRA_SOUND_TYPE, it)
                putExtra(TimerService.EXTRA_SOUND_RES_NAME, intent.getStringExtra(TimerService.EXTRA_SOUND_RES_NAME))
                putExtra(TimerService.EXTRA_SOUND_URI, intent.getStringExtra(TimerService.EXTRA_SOUND_URI))
                putExtra(TimerService.EXTRA_SOUND_NAME, intent.getStringExtra(TimerService.EXTRA_SOUND_NAME))
            }
        }
        context.startForegroundService(serviceIntent)
    }

    companion object {
        const val ALARM_NOTIFICATION_ID = 2001
    }
}
