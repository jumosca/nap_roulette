package com.naproulette.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.ALARM_NOTIFICATION_ID)
        // Stop the timer service which also stops sound
        context.stopService(Intent(context, TimerService::class.java))
    }
}
