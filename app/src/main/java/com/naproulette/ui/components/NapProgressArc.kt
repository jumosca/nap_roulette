package com.naproulette.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkFaint

@Composable
fun NapProgressArc(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Canvas(modifier = modifier.size(280.dp)) {
        val strokeWidth = 4.dp.toPx()
        val padding = strokeWidth / 2
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(padding, padding)

        // Background track — thin dashed ink line
        drawArc(
            color = InkFaint,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )
        )

        // Progress arc — solid ink
        if (animatedProgress > 0f) {
            drawArc(
                color = CinnabarRed,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Square)
            )
        }

        // Tick marks at quarters
        val center = Offset(size.width / 2, size.height / 2)
        val radius = arcSize.width / 2
        for (i in 0 until 12) {
            val angle = Math.toRadians(-90.0 + i * 30.0)
            val tickLength = if (i % 3 == 0) 12f else 6f
            val outerX = center.x + radius * kotlin.math.cos(angle).toFloat()
            val outerY = center.y + radius * kotlin.math.sin(angle).toFloat()
            val innerX = center.x + (radius - tickLength) * kotlin.math.cos(angle).toFloat()
            val innerY = center.y + (radius - tickLength) * kotlin.math.sin(angle).toFloat()
            drawLine(
                color = InkBlack.copy(alpha = 0.3f),
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = if (i % 3 == 0) 2f else 1f
            )
        }
    }
}
