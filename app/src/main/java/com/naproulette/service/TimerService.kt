package com.naproulette.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.naproulette.MainActivity
import com.naproulette.NapRouletteApp
import com.naproulette.R
import com.naproulette.domain.model.AlarmSound
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject lateinit var alarmSoundPlayer: AlarmSoundPlayer

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var countdownJob: Job? = null
    private var currentAlarmSound: AlarmSound = AlarmSound.default

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMillis = intent.getLongExtra(EXTRA_DURATION_MILLIS, 0L)
                if (durationMillis > 0) {
                    currentAlarmSound = soundFromIntent(intent)
                    startTimer(durationMillis)
                }
            }
            ACTION_STOP -> {
                cancelTimer()
                stopSelf()
            }
            ACTION_ALARM_FIRED -> {
                handleAlarmFired()
            }
            ACTION_DISMISS -> {
                dismissAlarm()
                stopSelf()
            }
            ACTION_SNOOZE -> {
                val snoozeMillis = intent.getLongExtra(EXTRA_DURATION_MILLIS, 5 * 60 * 1000L)
                snooze(snoozeMillis)
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        _endTimeMillis.value = endTime
        _remainingMillis.value = durationMillis
        _isRunning.value = true
        _isAlarmFiring.value = false

        startForeground(TIMER_NOTIFICATION_ID, buildTimerNotification(durationMillis))
        scheduleAlarm(durationMillis)
        startCountdownUpdates(endTime)
    }

    private fun scheduleAlarm(durationMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            when (val s = currentAlarmSound) {
                is AlarmSound.Bundled -> {
                    putExtra(EXTRA_SOUND_TYPE, "bundled")
                    putExtra(EXTRA_SOUND_RES_NAME, s.resName)
                }
                is AlarmSound.Custom -> {
                    putExtra(EXTRA_SOUND_TYPE, "custom")
                    putExtra(EXTRA_SOUND_URI, s.uri.toString())
                    putExtra(EXTRA_SOUND_NAME, s.name)
                }
            }
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, ALARM_REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = SystemClock.elapsedRealtime() + durationMillis
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAt,
            pendingIntent
        )
        Log.d("TimerService", "Alarm scheduled for ${durationMillis / 1000}s from now")
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, ALARM_REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun startCountdownUpdates(endTime: Long) {
        countdownJob?.cancel()
        countdownJob = serviceScope.launch {
            while (true) {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining <= 0) {
                    _remainingMillis.value = 0
                    break
                }
                _remainingMillis.value = remaining
                updateNotification(remaining)
                delay(1000)
            }
        }
    }

    private fun cancelTimer() {
        countdownJob?.cancel()
        cancelAlarm()
        _isRunning.value = false
        _remainingMillis.value = 0
        _endTimeMillis.value = 0
        _isAlarmFiring.value = false
        alarmSoundPlayer.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun handleAlarmFired() {
        countdownJob?.cancel()
        _isRunning.value = false
        _remainingMillis.value = 0
        _isAlarmFiring.value = true
        alarmSoundPlayer.play(currentAlarmSound)
    }

    private fun dismissAlarm() {
        alarmSoundPlayer.stop()
        _isAlarmFiring.value = false
        _isRunning.value = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        androidx.core.app.NotificationManagerCompat.from(this)
            .cancel(AlarmReceiver.ALARM_NOTIFICATION_ID)
    }

    private fun snooze(durationMillis: Long) {
        dismissAlarm()
        startTimer(durationMillis)
    }

    private fun buildTimerNotification(remainingMillis: Long): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_STOP
        }
        val cancelPendingIntent = PendingIntent.getService(
            this, 1, cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NapRouletteApp.TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Nap in progress")
            .setContentText("Time remaining: ${formatTime(remainingMillis)}")
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_timer, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(remainingMillis: Long) {
        val notification = buildTimerNotification(remainingMillis)
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(TIMER_NOTIFICATION_ID, notification)
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun soundFromIntent(intent: Intent): AlarmSound {
        return when (intent.getStringExtra(EXTRA_SOUND_TYPE)) {
            "bundled" -> {
                val resName = intent.getStringExtra(EXTRA_SOUND_RES_NAME) ?: return AlarmSound.default
                AlarmSound.allBundled.find { it.resName == resName } ?: AlarmSound.default
            }
            "custom" -> {
                val uriStr = intent.getStringExtra(EXTRA_SOUND_URI) ?: return AlarmSound.default
                val name = intent.getStringExtra(EXTRA_SOUND_NAME) ?: "Custom Sound"
                AlarmSound.Custom(android.net.Uri.parse(uriStr), name)
            }
            else -> AlarmSound.default
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        alarmSoundPlayer.stop()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.naproulette.action.START"
        const val ACTION_STOP = "com.naproulette.action.STOP"
        const val ACTION_ALARM_FIRED = "com.naproulette.action.ALARM_FIRED"
        const val ACTION_DISMISS = "com.naproulette.action.DISMISS"
        const val ACTION_SNOOZE = "com.naproulette.action.SNOOZE"
        const val EXTRA_DURATION_MILLIS = "duration_millis"
        const val EXTRA_SOUND_TYPE = "sound_type"
        const val EXTRA_SOUND_RES_NAME = "sound_res_name"
        const val EXTRA_SOUND_URI = "sound_uri"
        const val EXTRA_SOUND_NAME = "sound_name"

        private const val TIMER_NOTIFICATION_ID = 1001
        private const val ALARM_REQUEST_CODE = 100

        // Shared state for UI observation
        private val _remainingMillis = MutableStateFlow(0L)
        val remainingMillis: StateFlow<Long> = _remainingMillis.asStateFlow()

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        private val _endTimeMillis = MutableStateFlow(0L)
        val endTimeMillis: StateFlow<Long> = _endTimeMillis.asStateFlow()

        private val _isAlarmFiring = MutableStateFlow(false)
        val isAlarmFiring: StateFlow<Boolean> = _isAlarmFiring.asStateFlow()

        fun startTimer(context: Context, durationMillis: Long, sound: AlarmSound = AlarmSound.default) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION_MILLIS, durationMillis)
                when (sound) {
                    is AlarmSound.Bundled -> {
                        putExtra(EXTRA_SOUND_TYPE, "bundled")
                        putExtra(EXTRA_SOUND_RES_NAME, sound.resName)
                    }
                    is AlarmSound.Custom -> {
                        putExtra(EXTRA_SOUND_TYPE, "custom")
                        putExtra(EXTRA_SOUND_URI, sound.uri.toString())
                        putExtra(EXTRA_SOUND_NAME, sound.name)
                    }
                }
            }
            context.startForegroundService(intent)
        }

        fun stopTimer(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun dismissAlarm(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_DISMISS
            }
            context.startService(intent)
        }

        fun snooze(context: Context, durationMillis: Long = 5 * 60 * 1000L) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_SNOOZE
                putExtra(EXTRA_DURATION_MILLIS, durationMillis)
            }
            context.startService(intent)
        }
    }
}
