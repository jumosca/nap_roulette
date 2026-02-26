package com.naproulette.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.naproulette.data.local.PreferencesManager
import com.naproulette.data.repository.NapRepository
import com.naproulette.domain.model.AlarmSound
import com.naproulette.domain.model.NapPreset
import com.naproulette.domain.model.NapSession
import com.naproulette.domain.model.NapStats
import com.naproulette.domain.model.TimeRange
import com.naproulette.domain.model.TimerState
import com.naproulette.domain.usecase.CalculateNapStats
import com.naproulette.domain.usecase.GenerateRandomDuration
import com.naproulette.domain.usecase.GetNapVerdict
import com.naproulette.service.AlarmSoundPlayer
import com.naproulette.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NapViewModel @Inject constructor(
    private val application: Application,
    private val generateRandomDuration: GenerateRandomDuration,
    private val calculateNapStats: CalculateNapStats,
    private val getNapVerdict: GetNapVerdict,
    private val repository: NapRepository,
    private val preferences: PreferencesManager,
    private val alarmSoundPlayer: AlarmSoundPlayer
) : AndroidViewModel(application) {

    // Timer state from service
    val remainingMillis: StateFlow<Long> = TimerService.remainingMillis
    val isTimerRunning: StateFlow<Boolean> = TimerService.isRunning
    val isAlarmFiring: StateFlow<Boolean> = TimerService.isAlarmFiring

    // UI state
    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _totalDurationMillis = MutableStateFlow(0L)
    val totalDurationMillis: StateFlow<Long> = _totalDurationMillis.asStateFlow()

    private val _minMinutes = MutableStateFlow(10f)
    val minMinutes: StateFlow<Float> = _minMinutes.asStateFlow()

    private val _maxMinutes = MutableStateFlow(120f)
    val maxMinutes: StateFlow<Float> = _maxMinutes.asStateFlow()

    private val _selectedSound = MutableStateFlow<AlarmSound>(AlarmSound.default)
    val selectedSound: StateFlow<AlarmSound> = _selectedSound.asStateFlow()

    private val _verdict = MutableStateFlow<String?>(null)
    val verdict: StateFlow<String?> = _verdict.asStateFlow()

    private val _selectedPreset = MutableStateFlow<NapPreset?>(null)
    val selectedPreset: StateFlow<NapPreset?> = _selectedPreset.asStateFlow()

    private val _showStats = MutableStateFlow(false)
    val showStats: StateFlow<Boolean> = _showStats.asStateFlow()

    private val _previewingSound = MutableStateFlow<AlarmSound?>(null)
    val previewingSound: StateFlow<AlarmSound?> = _previewingSound.asStateFlow()

    // Nap stats
    val napStats: StateFlow<NapStats> = repository.getAllSessions()
        .map { calculateNapStats(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NapStats())

    // Nap session start time tracking
    private var sessionStartTime: Long = 0

    init {
        loadPreferences()
        observeServiceState()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            _minMinutes.value = preferences.minMinutes.first()
            _maxMinutes.value = preferences.maxMinutes.first()
        }
    }

    private fun observeServiceState() {
        viewModelScope.launch {
            combine(isTimerRunning, isAlarmFiring) { running, alarm ->
                when {
                    alarm -> TimerState.ALARM_FIRING
                    running -> TimerState.COUNTING_DOWN
                    else -> {
                        if (_timerState.value == TimerState.SPINNING) TimerState.SPINNING
                        else TimerState.IDLE
                    }
                }
            }.collect { state ->
                _timerState.value = state
            }
        }
    }

    fun updateRange(min: Float, max: Float) {
        _minMinutes.value = min
        _maxMinutes.value = max
        _selectedPreset.value = null
        viewModelScope.launch {
            preferences.setRange(min, max)
        }
    }

    fun clearPreset() {
        _selectedPreset.value = null
    }

    fun selectPreset(preset: NapPreset) {
        _selectedPreset.value = preset
        _minMinutes.value = preset.range.min.inWholeMinutes.toFloat()
        _maxMinutes.value = preset.range.max.inWholeMinutes.toFloat()
        viewModelScope.launch {
            preferences.setRange(_minMinutes.value, _maxMinutes.value)
        }
    }

    fun selectSound(sound: AlarmSound) {
        _selectedSound.value = sound
        viewModelScope.launch {
            when (sound) {
                is AlarmSound.Bundled -> preferences.setAlarmSound("bundled", sound.resName)
                is AlarmSound.Custom -> preferences.setCustomSound(sound.uri, sound.name)
            }
        }
    }

    fun previewSound(sound: AlarmSound) {
        android.util.Log.d("NapViewModel", "previewSound: ${sound.displayName}")
        _previewingSound.value = sound
        alarmSoundPlayer.preview(sound, onComplete = { _previewingSound.value = null })
    }

    fun stopPreview() {
        alarmSoundPlayer.stop()
        _previewingSound.value = null
    }

    fun spin() {
        _verdict.value = null
        _timerState.value = TimerState.SPINNING

        viewModelScope.launch {
            val range = TimeRange(
                min = _minMinutes.value.toInt().minutes,
                max = _maxMinutes.value.toInt().minutes
            )
            val duration = generateRandomDuration(range)
            _totalDurationMillis.value = duration.inWholeMilliseconds

            // Spin animation delay
            delay(2000)

            // Start the actual timer
            sessionStartTime = System.currentTimeMillis()
            TimerService.startTimer(application, duration.inWholeMilliseconds, _selectedSound.value)
            _timerState.value = TimerState.COUNTING_DOWN
        }
    }

    fun stop() {
        val planned = _totalDurationMillis.value
        val actual = planned - remainingMillis.value

        TimerService.stopTimer(application)
        _timerState.value = TimerState.IDLE

        if (actual > 60_000) { // Only save if nap was > 1 minute
            saveSession(planned, actual, wasCompleted = false)
        }
    }

    fun dismissAlarm() {
        val planned = _totalDurationMillis.value
        TimerService.dismissAlarm(application)
        _timerState.value = TimerState.IDLE

        _verdict.value = getNapVerdict(planned.milliseconds)
        saveSession(planned, planned, wasCompleted = true)
    }

    fun snooze() {
        TimerService.snooze(application)
    }

    fun clearVerdict() {
        _verdict.value = null
    }

    fun toggleStats() {
        _showStats.value = !_showStats.value
    }

    private fun saveSession(plannedMillis: Long, actualMillis: Long, wasCompleted: Boolean) {
        viewModelScope.launch {
            repository.saveSession(
                NapSession(
                    startTimeMillis = sessionStartTime,
                    plannedDurationMillis = plannedMillis,
                    actualDurationMillis = actualMillis,
                    wasCompleted = wasCompleted,
                    presetName = _selectedPreset.value?.displayName
                )
            )
        }
    }
}
