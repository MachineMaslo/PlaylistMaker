package com.machinemaslos.playlistmaker.domain.useCases

import android.content.Context
import com.machinemaslos.playlistmaker.data.repository.HistoryRepo
import com.machinemaslos.playlistmaker.data.web.Track
import java.util.LinkedList

class HistoryCase(context: Context) {
    private val historyRepo = HistoryRepo(context)

    fun getHistory() = historyRepo.getHistory()

    fun setHistory(tracks: LinkedList<Track>?) = historyRepo.setHistory(tracks)

    fun clearHistory() = setHistory(null)

    fun addTrack(track: Track) {
        val history: LinkedList<Track> = getHistory()

        val iterator = history.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().id == track.id) {
                iterator.remove()
            }
        }

        if (history.size + 1 > 10) {
            while (history.size + 1 > 10) {
                history.removeLast()
            }
        }

        history.addFirst(track)
        setHistory(history)
    }
}
