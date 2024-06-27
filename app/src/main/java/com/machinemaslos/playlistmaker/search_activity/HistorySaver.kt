package com.machinemaslos.playlistmaker.search_activity

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun setHistory(tracks: LinkedList<Track>) {
        sharedPrefs.edit().putString(SEARCH_HISTORY, gson.toJson(tracks)).apply()
    }

    companion object {
        const val SEARCH_HISTORY = "search_history"
        val SEARCH_HISTORY_DEFAULT: String = Gson().toJson(LinkedList<Track>())
    }

}