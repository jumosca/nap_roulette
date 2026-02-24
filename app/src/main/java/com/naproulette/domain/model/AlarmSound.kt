package com.naproulette.domain.model

import android.net.Uri

sealed class AlarmSound(val displayName: String) {

    sealed class Bundled(displayName: String, val resName: String) : AlarmSound(displayName) {
        data object GentleChime : Bundled("Gentle Chime", "gentle_chime")
        data object Birds : Bundled("Birds", "birds")
        data object OceanWaves : Bundled("Ocean Waves", "ocean_waves")
        data object CoffeeShop : Bundled("Coffee Shop", "coffee_shop")
        data object ClassicAlarm : Bundled("Classic Alarm", "classic_alarm")
    }

    data class Custom(val uri: Uri, val name: String) : AlarmSound(name)

    companion object {
        val allBundled: List<Bundled> = listOf(
            Bundled.GentleChime,
            Bundled.Birds,
            Bundled.OceanWaves,
            Bundled.CoffeeShop,
            Bundled.ClassicAlarm
        )

        val default: AlarmSound = Bundled.GentleChime
    }
}
