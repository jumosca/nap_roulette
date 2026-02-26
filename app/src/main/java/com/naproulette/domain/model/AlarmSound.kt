package com.naproulette.domain.model

import android.net.Uri

sealed class AlarmSound(val displayName: String) {

    sealed class Bundled(displayName: String, val resName: String) : AlarmSound(displayName) {
        // Casino sounds
        data object CasinoSounds : Bundled("Casino Sounds", "casino_sounds")
        data object Coins : Bundled("Coins", "coins")
        data object Roulette : Bundled("Roulette", "roulette")
        data object SlotMachine : Bundled("Slot Machine", "slot_machine")

        // Classic sounds
        data object GentleChime : Bundled("Gentle Chime", "gentle_chime")
        data object Birds : Bundled("Birds", "birds")
        data object OceanWaves : Bundled("Ocean Waves", "ocean_waves")
        data object CoffeeShop : Bundled("Coffee Shop", "coffee_shop")
        data object ClassicAlarm : Bundled("Classic Alarm", "classic_alarm")
    }

    data class Custom(val uri: Uri, val name: String) : AlarmSound(name)

    companion object {
        val casinoSounds: List<Bundled> = listOf(
            Bundled.CasinoSounds,
            Bundled.Coins,
            Bundled.Roulette,
            Bundled.SlotMachine
        )

        val classicSounds: List<Bundled> = listOf(
            Bundled.GentleChime,
            Bundled.Birds,
            Bundled.OceanWaves,
            Bundled.CoffeeShop,
            Bundled.ClassicAlarm
        )

        val allBundled: List<Bundled> = casinoSounds + classicSounds

        val default: AlarmSound = Bundled.GentleChime
    }
}
