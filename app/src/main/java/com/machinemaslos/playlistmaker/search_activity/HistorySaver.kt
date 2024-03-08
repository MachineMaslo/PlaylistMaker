package com.machinemaslos.playlistmaker.search_activity

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.machinemaslos.playlistmaker.SEARCH_HISTORY
import com.machinemaslos.playlistmaker.SEARCH_HISTORY_DEFAULT
import com.machinemaslos.playlistmaker.SHARED_PREFS
import java.util.LinkedList

class HistorySaver(context: Context) {

    val sharedPrefs: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    val gson = Gson()

    fun getHistory(): LinkedList<Track> {
        return gson.fromJson(
            sharedPrefs.getString( SEARCH_HISTORY, SEARCH_HISTORY_DEFAULT),
            object : TypeToken<LinkedList<Track>>() {}.type
        )
    }

}