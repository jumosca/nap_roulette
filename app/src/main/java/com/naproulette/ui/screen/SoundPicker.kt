package com.naproulette.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naproulette.domain.model.AlarmSound
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.CinnabarRedFaint
import com.naproulette.ui.theme.InkBlack
import com.naproulette.ui.theme.InkLight
import com.naproulette.ui.theme.InkMedium
import com.naproulette.ui.theme.VintageCard

@Composable
fun SoundPicker(
    selectedSound: AlarmSound,
    onSoundSelected: (AlarmSound) -> Unit,
    onPreview: (AlarmSound) -> Unit,
    onCustomSoundPicked: (Uri, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val name = "Custom Sound"
            onCustomSoundPicked(it, name)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            thickness = 1.dp,
            color = InkBlack.copy(alpha = 0.15f)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ALARM SOUND",
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 3.sp),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bundled sounds
        AlarmSound.allBundled.forEach { sound ->
            SoundRow(
                sound = sound,
                isSelected = selectedSound is AlarmSound.Bundled &&
                        (selectedSound as AlarmSound.Bundled).resName == sound.resName,
                onSelect = { onSoundSelected(sound) },
                onPreview = { onPreview(sound) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom upload button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .border(1.dp, InkBlack.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                .background(VintageCard)
                .clickable {
                    audioPickerLauncher.launch(arrayOf("audio/*"))
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = "Upload custom sound",
                tint = InkMedium,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (selectedSound is AlarmSound.Custom) {
                    "Custom: ${selectedSound.displayName}"
                } else {
                    "Upload custom sound"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = InkMedium
            )
        }
    }
}

@Composable
private fun SoundRow(
    sound: AlarmSound.Bundled,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(2.dp))
            .border(
                1.dp,
                if (isSelected) CinnabarRed.copy(alpha = 0.4f) else InkBlack.copy(alpha = 0.1f),
                RoundedCornerShape(2.dp)
            )
            .background(if (isSelected) CinnabarRedFaint else VintageCard)
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.MusicNote,
                contentDescription = null,
                tint = if (isSelected) CinnabarRed else InkLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = sound.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) CinnabarRed else InkBlack
            )
        }

        IconButton(onClick = onPreview, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Preview ${sound.displayName}",
                tint = InkLight
            )
        }
    }
}
