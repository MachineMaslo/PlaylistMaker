package com.machinemaslos.playlistmaker
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.machinemaslos.playlistmaker.search_activity.ExtendedTrack
import com.machinemaslos.playlistmaker.search_activity.dpToPx
import java.util.Locale

class PlayerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { finish() }

        val track = intent.getSerializableExtra("track") as ExtendedTrack

        Glide.with(applicationContext)
            .load(track.artworkUrl.replaceAfterLast("/", "512x512bb.jpg"))
            .transform(RoundedCorners(24.dpToPx(this)))
            .placeholder(R.drawable.placeholder)
            .into(findViewById(R.id.ivTrackCover))
        findViewById<TextView>(R.id.tvTrackTitle).text = track.name
        findViewById<TextView>(R.id.tvTrackSubtitle).text = track.artist
        findViewById<TextView>(R.id.tvAlbum).text = track.album
        findViewById<TextView>(R.id.tvYear).text = track.year.substring(0,4)
        findViewById<TextView>(R.id.tvCountry).text = track.country
        findViewById<TextView>(R.id.tvGenre).text = track.genre
        findViewById<TextView>(R.id.tvDuration).text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.time.toLong())
    }
}