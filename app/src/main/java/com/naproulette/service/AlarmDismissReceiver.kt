package com.naproulette.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.ALARM_NOTIFICATION_ID)
        // Send ACTION_DISMISS to the service so it resets _isAlarmFiring and stops sound cleanly.
        // stopService() would skip dismissAlarm() and leave _isAlarmFiring = true.
        context.startService(Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_DISMISS
        })
    }
}
