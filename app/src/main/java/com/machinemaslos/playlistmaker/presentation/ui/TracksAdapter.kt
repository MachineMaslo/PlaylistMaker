package com.machinemaslos.playlistmaker.presentation.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.data.web.Track
import com.machinemaslos.playlistmaker.presentation.presenters.HistoryInteractor
import com.machinemaslos.playlistmaker.presentation.presenters.SearchInteractor

class TracksAdapter(private val tracks: List<Track>,
                    private val searchInteractor: SearchInteractor, private val historyInteractor: HistoryInteractor)
                    : RecyclerView.Adapter<TrackHolder>() {
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

            historyInteractor.addTrack(track)
            searchInteractor.lookUpSearch(track.id)
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