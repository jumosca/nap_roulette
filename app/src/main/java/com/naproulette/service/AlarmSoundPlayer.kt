package com.naproulette.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.naproulette.domain.model.AlarmSound
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    fun play(sound: AlarmSound, vibrate: Boolean = true) {
        stop()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(audioAttributes)
            when (sound) {
                is AlarmSound.Bundled -> {
                    val resId = context.resources.getIdentifier(
                        sound.resName, "raw", context.packageName
                    )
                    if (resId != 0) {
                        val afd = context.resources.openRawResourceFd(resId)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    } else {
                        // Fallback: use default notification sound
                        setDataSource(
                            context,
                            android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
                        )
                    }
                }
                is AlarmSound.Custom -> {
                    setDataSource(context, sound.uri)
                }
            }
            isLooping = true
            prepare()
            start()
        }

        if (vibrate) {
            startVibration()
        }
    }

    fun preview(sound: AlarmSound) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(audioAttributes)
            when (sound) {
                is AlarmSound.Bundled -> {
                    val resId = context.resources.getIdentifier(
                        sound.resName, "raw", context.packageName
                    )
                    if (resId != 0) {
                        val afd = context.resources.openRawResourceFd(resId)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    }
                }
                is AlarmSound.Custom -> {
                    setDataSource(context, sound.uri)
                }
            }
            isLooping = false
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    private fun startVibration() {
        vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
            ?.defaultVibrator
            ?: context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }
}
