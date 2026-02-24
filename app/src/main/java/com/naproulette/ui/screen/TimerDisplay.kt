package com.naproulette.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkLight
import com.naproulette.ui.theme.VintageSerif

@Composable
fun TimerDisplay(
    remainingMillis: Long,
    modifier: Modifier = Modifier
) {
    val totalSeconds = remainingMillis / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()

    val timeText = if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = timeText,
            transitionSpec = {
                (fadeIn(tween(150)) + slideInVertically(tween(150)) { -it / 4 })
                    .togetherWith(fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 4 })
            },
            label = "timer"
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = VintageSerif,
                    fontSize = if (hours > 0) 60.sp else 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
            )
        }
    }
}

@Composable
fun IdleTimerDisplay(
    minMinutes: Float,
    maxMinutes: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "${minMinutes.toInt()}",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = VintageSerif,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                color = InkBlack,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = " min",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 16.sp,
                color = InkLight,
                fontWeight = FontWeight.Light
            )
        )
        Text(
            text = " – ",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 36.sp,
                lineHeight = 40.sp,
                color = InkLight,
                fontWeight = FontWeight.Light
            )
        )
        Text(
            text = "${maxMinutes.toInt()}",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = VintageSerif,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                color = InkBlack,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = " min",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 16.sp,
                color = InkLight,
                fontWeight = FontWeight.Light
            )
        )
    }
}
