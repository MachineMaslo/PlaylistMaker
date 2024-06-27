package com.machinemaslos.playlistmaker.search_activity

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.SEARCH_HISTORY
import com.machinemaslos.playlistmaker.SEARCH_HISTORY_DEFAULT
import java.util.LinkedList

class TracksAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackHolder>() {
    private lateinit var context: Context

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_holder, parent, false)
        context = parent.context
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.songButton.setOnClickListener {
            if (!clickDebounce()) { // Check if a click is allowed
                return@setOnClickListener
            }

            val historySaver = HistorySaver(context)
            val history: LinkedList<Track> = historySaver.getHistory()
            Log.d("History", "$history")
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
            historySaver.setHistory(history)
            (context as SearchActivity).lookUpSearch(track.id)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}