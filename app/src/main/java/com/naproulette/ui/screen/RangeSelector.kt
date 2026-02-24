package com.naproulette.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.CinnabarRedDark
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkFaint
import com.naproulette.ui.theme.InkLight

@Composable
fun RangeSelector(
    minMinutes: Float,
    maxMinutes: Float,
    onRangeChanged: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValues by remember(minMinutes, maxMinutes) {
        mutableStateOf(minMinutes..maxMinutes)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${sliderValues.start.toInt()} min",
                style = MaterialTheme.typography.bodyLarge,
                color = InkBlack
            )
            Text(
                text = "${sliderValues.endInclusive.toInt()} min",
                style = MaterialTheme.typography.bodyLarge,
                color = InkBlack
            )
        }

        RangeSlider(
            value = sliderValues,
            onValueChange = { range ->
                if (range.endInclusive - range.start >= 5f) {
                    sliderValues = range
                }
            },
            onValueChangeFinished = {
                onRangeChanged(sliderValues.start, sliderValues.endInclusive)
            },
            valueRange = 1f..180f,
            steps = 178,
            colors = SliderDefaults.colors(
                thumbColor = CinnabarRed,
                activeTrackColor = CinnabarRed,
                inactiveTrackColor = InkFaint,
                activeTickColor = CinnabarRedDark,
                inactiveTickColor = InkFaint
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 min", style = MaterialTheme.typography.bodyMedium, color = InkLight)
            Text("3 hrs", style = MaterialTheme.typography.bodyMedium, color = InkLight)
        }
    }
}
