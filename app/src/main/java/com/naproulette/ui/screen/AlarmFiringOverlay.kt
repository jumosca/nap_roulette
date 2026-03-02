package com.naproulette.ui.screen

import android.os.Build
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.naproulette.R
import com.naproulette.ui.components.GrainOverlay
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkMedium
import com.naproulette.ui.theme.VintagePaper
import com.naproulette.ui.theme.VintageSerif

@Composable
fun AlarmFiringOverlay(
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

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
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.alarm_view)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Alarm",
                modifier = Modifier.size(280.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WAKE UP",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = VintageSerif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = InkBlack
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your nap is over",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = VintageSerif
                ),
                color = InkMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinnabarRed
                ),
                shape = RoundedCornerShape(16.dp),
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
                shape = RoundedCornerShape(16.dp),
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
