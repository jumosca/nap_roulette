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

    private val alarmAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val previewAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    fun play(sound: AlarmSound, vibrate: Boolean = true) {
        stop()

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(alarmAttributes)
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
        } catch (e: Exception) {
            android.util.Log.e("AlarmSoundPlayer", "Failed to play ${sound.displayName}", e)
            mediaPlayer?.release()
            mediaPlayer = null
            // Fall back to the default bundled sound so the alarm always fires audibly
            if (sound != AlarmSound.default) {
                play(AlarmSound.default, vibrate = false)
                return
            }
        }

        if (vibrate) startVibration()
    }

    fun preview(sound: AlarmSound, onComplete: () -> Unit = {}) {
        android.util.Log.d("AlarmSoundPlayer", "preview called: ${sound.displayName}")
        stop()
        try {
            val player = MediaPlayer()
            player.setAudioAttributes(previewAttributes)
            when (sound) {
                is AlarmSound.Bundled -> {
                    val resId = context.resources.getIdentifier(
                        sound.resName, "raw", context.packageName
                    )
                    if (resId == 0) { player.release(); return }
                    val afd = context.resources.openRawResourceFd(resId)
                    player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                }
                is AlarmSound.Custom -> {
                    player.setDataSource(context, sound.uri)
                }
            }
            player.isLooping = false
            player.setOnPreparedListener {
                android.util.Log.d("AlarmSoundPlayer", "prepared, starting playback")
                it.start()
            }
            player.setOnErrorListener { _, _, _ -> player.release(); mediaPlayer = null; onComplete(); true }
            player.setOnCompletionListener { it.release(); mediaPlayer = null; onComplete() }
            player.prepareAsync()
            mediaPlayer = player
        } catch (e: Exception) {
            android.util.Log.e("AlarmSoundPlayer", "preview failed", e)
            mediaPlayer?.release()
            mediaPlayer = null
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

    @Suppress("DEPRECATION")
    private fun startVibration() {
        vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
            ?.defaultVibrator
            ?: context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }
}
