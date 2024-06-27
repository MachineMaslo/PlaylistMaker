package com.machinemaslos.playlistmaker.presentation.presenters

import android.content.Context
import com.machinemaslos.playlistmaker.data.web.Track
import com.machinemaslos.playlistmaker.domain.useCases.HistoryCase
import java.util.LinkedList

class HistoryInteractor(context: Context) {
    private val historyCase = HistoryCase(context)

    fun getHistory() = historyCase.getHistory()

    fun setHistory(tracks: LinkedList<Track>) = historyCase.setHistory(tracks)

    fun clearHistory() = historyCase.clearHistory()

    fun addTrack(track: Track) = historyCase.addTrack(track)
}