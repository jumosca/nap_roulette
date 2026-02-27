package com.naproulette.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.naproulette.R

@Composable
fun SpinButton(
    isSpinning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Idle: gentle pulse
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Spinning: continuous rotation
    val spinTransition = rememberInfiniteTransition(label = "spin")
    val spinRotation by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_rotation"
    )

    // Spinning: scale up
    val spinScale = remember { Animatable(1f) }
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            spinScale.animateTo(1.6f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        } else {
            spinScale.animateTo(1f, animationSpec = tween(300))
        }
    }

    val currentScale = if (isSpinning) spinScale.value else pulse
    val currentRotation = if (isSpinning) spinRotation else 0f

    Image(
        painter = painterResource(id = if (isSpinning) R.drawable.roulette_spin else R.drawable.roulette_homepage),
        contentDescription = if (isSpinning) "Spinning..." else "Spin the roulette",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(200.dp)
            .scale(currentScale)
            .rotate(currentRotation)
            .then(if (isSpinning) Modifier.clip(CircleShape) else Modifier)
            .clickable(enabled = !isSpinning) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
    )
}
