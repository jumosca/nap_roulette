package com.naproulette

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.naproulette.ui.screen.NapScreen
import com.naproulette.ui.theme.CinnabarRed
import com.naproulette.ui.theme.NapRouletteTheme
import com.naproulette.ui.theme.VintagePaper
import com.naproulette.viewmodel.NapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NapViewModel by viewModels()
    private var showAlarmPermissionDialog by mutableStateOf(false)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* User granted or denied — we handle gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestNotificationPermission()

        setContent {
            NapRouletteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VintagePaper
                ) {
                    NapScreen(viewModel = viewModel)
                }

                if (showAlarmPermissionDialog) {
                    AlertDialog(
                        onDismissRequest = { showAlarmPermissionDialog = false },
                        title = {
                            Text(
                                "Alarm permission needed",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        text = {
                            Text(
                                "Nap Roulette needs permission to set exact alarms so your " +
                                "nap timer fires reliably even when your phone is asleep. " +
                                "Tap \"Open settings\" and enable \"Alarms & reminders\".",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showAlarmPermissionDialog = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    startActivity(
                                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    )
                                }
                            }) {
                                Text("Open settings", color = CinnabarRed)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAlarmPermissionDialog = false }) {
                                Text("Not now")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkExactAlarmPermission()
    }

    // SCHEDULE_EXACT_ALARM requires explicit user approval only on Android 12 (API 31/32).
    // Android 13+ auto-grants USE_EXACT_ALARM for alarm/timer apps — no prompt needed.
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S ||
            Build.VERSION.SDK_INT == Build.VERSION_CODES.S_V2
        ) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                showAlarmPermissionDialog = true
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
