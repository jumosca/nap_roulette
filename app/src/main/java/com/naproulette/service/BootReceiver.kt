package com.naproulette.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted — timer state was lost (AlarmManager cleared)")
            // AlarmManager alarms are cleared on reboot.
            // A future enhancement could persist the alarm end time in DataStore
            // and re-schedule here if the alarm time hasn't passed.
        }
    }
}
