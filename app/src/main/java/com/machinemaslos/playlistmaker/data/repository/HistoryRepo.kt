package com.machinemaslos.playlistmaker.data.repository

import android.content.Context
import com.machinemaslos.playlistmaker.data.dto.HistorySaver
import com.machinemaslos.playlistmaker.data.web.Track
import java.util.LinkedList

class HistoryRepo(context: Context) {
    private val historySaver = HistorySaver(context)

    fun getHistory() = historySaver.getHistory()

    fun setHistory(tracks: LinkedList<Track>) = historySaver.setHistory(tracks)
}