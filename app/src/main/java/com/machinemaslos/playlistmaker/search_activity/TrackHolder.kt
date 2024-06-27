package com.machinemaslos.playlistmaker.search_activity

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.SEARCH_HISTORY
import com.machinemaslos.playlistmaker.SEARCH_HISTORY_DEFAULT
import com.machinemaslos.playlistmaker.SHARED_PREFS
import java.util.LinkedList
import java.util.Locale
import java.util.Queue

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

}
fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics).toInt()
}