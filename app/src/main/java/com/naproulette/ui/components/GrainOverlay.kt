package com.naproulette.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun GrainOverlay(
    modifier: Modifier = Modifier,
    density: Float = 0.08f,
    alpha: Float = 0.06f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width.toInt()
        val height = size.height.toInt()
        val step = 3
        val random = Random(42) // Fixed seed for consistent grain

        for (x in 0 until width step step) {
            for (y in 0 until height step step) {
                if (random.nextFloat() < density) {
                    val grainAlpha = random.nextFloat() * alpha
                    drawCircle(
                        color = Color.Black.copy(alpha = grainAlpha),
                        radius = random.nextFloat() * 1.5f + 0.5f,
                        center = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
                    )
                }
            }
        }
    }
}
