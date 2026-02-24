package com.naproulette.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.naproulette.domain.model.NapPreset
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.CinnabarRedDark
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkLight
import com.naproulette.ui.theme.VintageCard
import com.naproulette.ui.theme.VintageSerif

private data class CardLayout(
    val rotation: Float,
    val offsetX: Float,
    val offsetY: Float
)

private val cardLayouts = listOf(
    CardLayout(rotation = -35f, offsetX = -68f, offsetY = 20f),
    CardLayout(rotation = -12f, offsetX = -24f, offsetY = 0f),
    CardLayout(rotation = 12f, offsetX = 24f, offsetY = 0f),
    CardLayout(rotation = 38f, offsetX = 68f, offsetY = 22f)
)

@Composable
fun PresetCardFan(
    selectedPreset: NapPreset?,
    onPresetSelected: (NapPreset) -> Unit,
    onPresetConfirmed: (NapPreset) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical "NAP PRESETS" label on the left
        Text(
            text = "NAP PRESETS",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                letterSpacing = 3.sp,
                color = InkBlack
            ),
            modifier = Modifier
                .offset(x = (-8).dp, y = 30.dp)
                .graphicsLayer { rotationZ = -90f }
                .border(1.dp, InkBlack.copy(alpha = 0.3f))
                .padding(horizontal = 14.dp, vertical = 14.dp)
        )

        // Card fan
        Box(
            modifier = Modifier
                .weight(1f)
                .height(220.dp)
                .offset(x = (-24).dp, y = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            NapPreset.entries.forEachIndexed { index, preset ->
                val layout = cardLayouts[index]
                val isSelected = selectedPreset == preset

                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1f,
                    animationSpec = spring(stiffness = 300f),
                    label = "cardScale"
                )

                PresetCard(
                    preset = preset,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onPresetConfirmed(preset)
                        } else {
                            onPresetSelected(preset)
                        }
                    },
                    modifier = Modifier
                        .zIndex(if (isSelected) 10f else index.toFloat())
                        .offset(x = layout.offsetX.dp, y = layout.offsetY.dp)
                        .graphicsLayer {
                            rotationZ = layout.rotation
                            scaleX = animatedScale
                            scaleY = animatedScale
                        }
                )
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: NapPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(7.dp)
    val borderColor = if (isSelected) CinnabarRedDark else InkBlack.copy(alpha = 0.6f)
    val borderWidth = if (isSelected) 2.dp else 0.5.dp
    val suitColor = if (preset.isRedSuit) CinnabarRedDark else InkBlack

    Surface(
        modifier = modifier
            .size(width = 85.dp, height = 115.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 3.dp,
                shape = shape,
                ambientColor = InkBlack.copy(alpha = 0.3f),
                spotColor = InkBlack.copy(alpha = 0.3f)
            )
            .border(borderWidth, borderColor, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        color = VintageCard
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Preset name
            Text(
                text = preset.displayName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = VintageSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.3.sp,
                    color = InkBlack
                ),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            // Duration range — tight below name
            Text(
                text = "${preset.range.min.inWholeMinutes}–${preset.range.max.inWholeMinutes}m",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    color = InkLight
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 1.dp)
            )

            // Suit symbol — vertically centered in remaining space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = preset.suit,
                    fontSize = 32.sp,
                    color = suitColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

