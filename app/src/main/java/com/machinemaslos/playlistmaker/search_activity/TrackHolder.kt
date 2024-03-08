package com.machinemaslos.playlistmaker.search_activity

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.machinemaslos.playlistmaker.R
import java.util.Locale

class TrackHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView) {
    private val songTitle: TextView
    private val songSubtitle: TextView
    private val songCover: ImageView

    init {
        songTitle = parentView.findViewById(R.id.songTitle)
        songSubtitle = parentView.findViewById(R.id.songSubtitle)
        songCover = parentView.findViewById(R.id.songCover)
    }

    fun bind(track: Track) {
        songTitle.text = track.name
        songSubtitle.text = "${track.artist} • ${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.time.toLong()) }"
        Glide.with(parentView.context.applicationContext)
            .load(track.artworkUrl)
            .transform(RoundedCorners(dpToPx(4f, parentView.context)))
            .placeholder(R.drawable.placeholder)
            .into(songCover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}