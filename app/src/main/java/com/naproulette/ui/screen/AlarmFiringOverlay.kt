package com.naproulette.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naproulette.ui.components.GrainOverlay
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkMedium
import com.naproulette.ui.theme.VintagePaper

@Composable
fun AlarmFiringOverlay(
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "alarm")
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alarm_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VintagePaper),
        contentAlignment = Alignment.Center
    ) {
        GrainOverlay()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = "Alarm",
                tint = CinnabarRed,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WAKE UP",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = InkBlack
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your nap is over",
                style = MaterialTheme.typography.bodyLarge,
                color = InkMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinnabarRed
                ),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "DISMISS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 18.sp,
                        letterSpacing = 3.sp,
                        color = VintagePaper
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSnooze,
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = InkBlack
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(InkBlack.copy(alpha = 0.3f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Snooze,
                    contentDescription = null,
                    tint = InkBlack,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SNOOZE 5 MIN",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = InkBlack
                    )
                )
            }
        }
    }
}
