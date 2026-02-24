package com.naproulette.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.naproulette.NapRouletteApp
import com.naproulette.R
import com.naproulette.ui.screen.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm fired!")

        // Launch full-screen alarm activity
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss action
        val dismissIntent = Intent(context, AlarmDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 1, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NapRouletteApp.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Wake up!")
            .setContentText("Your nap is over!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.ic_alarm, "Dismiss", dismissPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSilent(true) // AlarmSoundPlayer handles all audio
            .build()

        try {
            NotificationManagerCompat.from(context).notify(ALARM_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            Log.e("AlarmReceiver", "No notification permission", e)
        }

        // Start the alarm activity directly as well
        context.startActivity(alarmIntent)
    }

    companion object {
        const val ALARM_NOTIFICATION_ID = 2001
    }
}
