package com.naproulette.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naproulette.domain.model.AlarmSound
import com.naproulette.domain.model.NapPreset
import com.naproulette.domain.model.TimerState
import com.naproulette.ui.components.GrainOverlay
import com.naproulette.ui.components.PresetCardFan
import com.naproulette.ui.components.NapProgressArc
import com.naproulette.ui.components.SpinButton
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.CinnabarRedFaint
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkDark
import com.naproulette.ui.theme.InkLight
import com.naproulette.ui.theme.InkMedium
import com.naproulette.ui.theme.VintageCard
import com.naproulette.ui.theme.RouletteGreen
import com.naproulette.ui.theme.VintageGold
import com.naproulette.ui.theme.VintagePaper
import com.naproulette.ui.theme.VintagePaperDark
import com.naproulette.viewmodel.NapViewModel

@Composable
fun NapScreen(viewModel: NapViewModel) {
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val remainingMillis by viewModel.remainingMillis.collectAsStateWithLifecycle()
    val totalDurationMillis by viewModel.totalDurationMillis.collectAsStateWithLifecycle()
    val minMinutes by viewModel.minMinutes.collectAsStateWithLifecycle()
    val maxMinutes by viewModel.maxMinutes.collectAsStateWithLifecycle()
    val selectedSound by viewModel.selectedSound.collectAsStateWithLifecycle()
    val verdict by viewModel.verdict.collectAsStateWithLifecycle()
    val selectedPreset by viewModel.selectedPreset.collectAsStateWithLifecycle()
    val showStats by viewModel.showStats.collectAsStateWithLifecycle()
    val napStats by viewModel.napStats.collectAsStateWithLifecycle()
    val previewingSound by viewModel.previewingSound.collectAsStateWithLifecycle()

    // Full-screen alarm overlay
    if (timerState == TimerState.ALARM_FIRING) {
        AlarmFiringOverlay(
            onDismiss = { viewModel.dismissAlarm() },
            onSnooze = { viewModel.snooze() }
        )
        return
    }

    val screenBackground by animateColorAsState(
        targetValue = if (timerState == TimerState.SPINNING) RouletteGreen else VintagePaper,
        label = "screen_background"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        // Grain texture overlay
        GrainOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { viewModel.clearPreset() }
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Green baize background — header + roulette + subtitle
            val greenPadding by animateDpAsState(
                targetValue = if (timerState == TimerState.SPINNING) 96.dp else 24.dp,
                label = "green_padding"
            )
            val greenBackground by animateColorAsState(
                targetValue = if (timerState == TimerState.COUNTING_DOWN) Color.Transparent else RouletteGreen,
                label = "green_background"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(greenBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header (only when idle)
                    AnimatedVisibility(
                        visible = timerState == TimerState.IDLE,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 1.dp, color = VintagePaper.copy(alpha = 0.5f))
                            Text(
                                text = "NAP ROULETTE",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    letterSpacing = 3.sp,
                                    color = VintagePaper
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                            )
                            HorizontalDivider(thickness = 1.dp, color = VintagePaper.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Roulette / timer
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = greenPadding)
                    ) {
                        Spacer(modifier = Modifier.height(
                            if (timerState == TimerState.COUNTING_DOWN) 56.dp else 0.dp
                        ))

                        // Roulette / Spin button
                        when (timerState) {
                            TimerState.IDLE -> {
                                SpinButton(
                                    isSpinning = false,
                                    onClick = {
                                        viewModel.clearVerdict()
                                        viewModel.spin()
                                    }
                                )
                            }
                            TimerState.SPINNING -> {
                                SpinButton(isSpinning = true, onClick = {})
                            }
                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Timer display (countdown only)
                        if (timerState == TimerState.COUNTING_DOWN) {
                            Box(contentAlignment = Alignment.Center) {
                                val progress = if (totalDurationMillis > 0) {
                                    remainingMillis.toFloat() / totalDurationMillis.toFloat()
                                } else 0f

                                NapProgressArc(progress = progress)
                                TimerDisplay(remainingMillis = remainingMillis)
                            }
                        }

                        // Stop button (below timer during countdown)
                        AnimatedVisibility(
                            visible = timerState == TimerState.COUNTING_DOWN,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(24.dp))
                                IconButton(
                                    onClick = { viewModel.stop() },
                                    modifier = Modifier
                                        .size(72.dp)
                                        .border(2.dp, CinnabarRed, RoundedCornerShape(50))
                                        .background(CinnabarRedFaint, RoundedCornerShape(50))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop",
                                        tint = CinnabarRed,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Subtitle (only when spinning)
                    AnimatedVisibility(
                        visible = timerState == TimerState.SPINNING,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = "Today is Your Lucky Day!",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Cursive,
                                fontStyle = FontStyle.Italic,
                                fontSize = 28.sp,
                                color = VintagePaper
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                        )
                    }
                }
            }

            // Range slider (below numbers)
            AnimatedVisibility(visible = timerState == TimerState.IDLE) {
                Column {
                    Spacer(modifier = Modifier.height(28.dp))
                    RangeSelector(
                        minMinutes = minMinutes,
                        maxMinutes = maxMinutes,
                        onRangeChanged = { min, max -> viewModel.updateRange(min, max) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets — playing card fan
            AnimatedVisibility(visible = timerState == TimerState.IDLE) {
                PresetCardFan(
                    selectedPreset = selectedPreset,
                    onPresetSelected = { viewModel.selectPreset(it) },
                    onPresetConfirmed = {
                        viewModel.clearVerdict()
                        viewModel.spin()
                    }
                )
            }

            // Verdict card
            AnimatedVisibility(
                visible = verdict != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                verdict?.let { text ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp)
                            .border(1.dp, InkBlack.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                            .background(VintageCard)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = InkDark,
                                fontStyle = FontStyle.Italic
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Sound picker (only when idle)
            AnimatedVisibility(visible = timerState == TimerState.IDLE) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    SoundPicker(
                        selectedSound = selectedSound,
                        previewingSound = previewingSound,
                        onSoundSelected = { viewModel.selectSound(it) },
                        onPreview = { viewModel.previewSound(it) },
                        onStopPreview = { viewModel.stopPreview() },
                        onCustomSoundPicked = { uri, name ->
                            viewModel.selectSound(AlarmSound.Custom(uri, name))
                        }
                    )
                }
            }

            // Stats section (only when idle)
            AnimatedVisibility(visible = timerState == TimerState.IDLE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Collapsible header
                    val statsChevron by animateFloatAsState(
                        targetValue = if (showStats) 180f else 0f,
                        label = "stats_chevron"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.toggleStats() }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NAP STATISTICS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                letterSpacing = 3.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (showStats) "Collapse" else "Expand",
                            tint = InkMedium,
                            modifier = Modifier.rotate(statsChevron)
                        )
                    }

                    // Expandable content
                    AnimatedVisibility(
                        visible = showStats,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            HorizontalDivider(thickness = 1.dp, color = InkBlack.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))
                            StatRow("Total Naps", "${napStats.totalNaps}")
                            StatRow("Completed", "${napStats.completedNaps}")
                            StatRow("Avg Duration", formatDuration(napStats.averageDuration.inWholeMilliseconds))
                            StatRow("Total Time", formatDuration(napStats.totalNapTime.inWholeMilliseconds))
                            StatRow("Longest", formatDuration(napStats.longestNap.inWholeMilliseconds))
                            StatRow("Streak", "${napStats.currentStreak} naps")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = InkMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = InkBlack
        ))
    }
}

private fun formatDuration(millis: Long): String {
    if (millis <= 0) return "\u2014"
    val totalMinutes = millis / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
