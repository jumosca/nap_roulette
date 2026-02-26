package com.naproulette.ui.screen

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import android.net.Uri
import com.naproulette.domain.model.AlarmSound
import com.naproulette.service.AlarmReceiver
import com.naproulette.service.AlarmSoundPlayer
import com.naproulette.service.TimerService
import com.naproulette.ui.theme.NapRouletteTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    @Inject lateinit var alarmSoundPlayer: AlarmSoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wake screen and show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Start playing alarm — use sound selected by user, fall back to default
        val sound: AlarmSound = when (intent.getStringExtra(TimerService.EXTRA_SOUND_TYPE)) {
            "bundled" -> {
                val resName = intent.getStringExtra(TimerService.EXTRA_SOUND_RES_NAME)
                AlarmSound.allBundled.find { it.resName == resName } ?: AlarmSound.default
            }
            "custom" -> {
                val uriStr = intent.getStringExtra(TimerService.EXTRA_SOUND_URI)
                val name = intent.getStringExtra(TimerService.EXTRA_SOUND_NAME) ?: "Custom Sound"
                if (uriStr != null) AlarmSound.Custom(Uri.parse(uriStr), name) else AlarmSound.default
            }
            else -> AlarmSound.default
        }
        alarmSoundPlayer.play(sound, vibrate = true)

        setContent {
            NapRouletteTheme {
                AlarmFiringOverlay(
                    onDismiss = { dismiss() },
                    onSnooze = { snooze() }
                )
            }
        }
    }

    private fun dismiss() {
        alarmSoundPlayer.stop()
        TimerService.dismissAlarm(this)
        androidx.core.app.NotificationManagerCompat.from(this)
            .cancel(AlarmReceiver.ALARM_NOTIFICATION_ID)
        finishAndRemoveTask()
    }

    private fun snooze() {
        alarmSoundPlayer.stop()
        TimerService.snooze(this)
        androidx.core.app.NotificationManagerCompat.from(this)
            .cancel(AlarmReceiver.ALARM_NOTIFICATION_ID)
        finishAndRemoveTask()
    }

    override fun onDestroy() {
        alarmSoundPlayer.stop()
        super.onDestroy()
    }
}
