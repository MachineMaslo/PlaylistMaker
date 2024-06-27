package com.machinemaslos.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.machinemaslos.playlistmaker.data.web.Track
import java.util.LinkedList

const val SHARED_PREFS = "shared_preferences"

const val NIGHT_MODE = "night_mode"

class App : Application() {

    private lateinit var sharedPreferences: SharedPreferences
    var mode: Int = 1

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        mode = sharedPreferences.getInt(NIGHT_MODE, 1)
        switchTheme(mode)
    }

    fun switchTheme(switchMode: Int) {
        AppCompatDelegate.setDefaultNightMode(switchMode)
        sharedPreferences.edit().putInt(NIGHT_MODE, switchMode).apply()
        mode = switchMode
    }
}

const val EX_TRACK = "extended_track"