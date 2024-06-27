package com.machinemaslos.playlistmaker.presentation.ui

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.data.web.Track
import java.util.Locale

class TrackHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView) {
    private val songTitle: TextView = parentView.findViewById(R.id.songTitle)
    private val songSubtitle: TextView = parentView.findViewById(R.id.songSubtitle)
    private val songCover: ImageView = parentView.findViewById(R.id.songCover)
    val songButton: Button = parentView.findViewById(R.id.songButton)

    fun bind(track: Track) {
        songTitle.text = track.name
        songSubtitle.text = "${track.artist} • ${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.time.toLong())}"
        Glide.with(parentView.context.applicationContext)
            .load(track.artworkUrl)
            .transform(RoundedCorners(4.dpToPx(parentView.context)))
            .placeholder(R.drawable.placeholder)
            .into(songCover)
    }
    companion object {

        fun Int.dpToPx(context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                context.resources.displayMetrics).toInt()
        }

    }
}