package com.machinemaslos.playlistmaker.data.dto

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.machinemaslos.playlistmaker.SEARCH_HISTORY
import com.machinemaslos.playlistmaker.SEARCH_HISTORY_DEFAULT
import com.machinemaslos.playlistmaker.SHARED_PREFS
import com.machinemaslos.playlistmaker.data.web.Track
import java.util.LinkedList

class HistorySaver(context: Context) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHistory(): LinkedList<Track> {
        return gson.fromJson(
            sharedPrefs.getString( SEARCH_HISTORY, SEARCH_HISTORY_DEFAULT),
            object : TypeToken<LinkedList<Track>>() {}.type
        )
    }
    fun setHistory(tracks: LinkedList<Track>?) {
        sharedPrefs.edit().putString(SEARCH_HISTORY, gson.toJson(tracks)).apply()
    }

}