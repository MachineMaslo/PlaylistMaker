package com.machinemaslos.playlistmaker.presentation.ui
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.machinemaslos.playlistmaker.EX_TRACK
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.data.web.LookUpTrack
import com.machinemaslos.playlistmaker.presentation.ui.TrackHolder.Companion.dpToPx
import java.util.Locale

class PlayerActivity: AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()

    private val handler by lazy { Handler(this.mainLooper) }
    private lateinit var updatePlaybackTimeRunnable: Runnable

    private lateinit var bPlay: ImageButton
    private lateinit var tvPlaybackTime: TextView

    private val pauseTrackImg by lazy { getDrawable(R.drawable.pause_track) }
    private val playTrackImg by lazy { getDrawable(R.drawable.play_track) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { finish() }

        val track = intent.getSerializableExtra(EX_TRACK) as LookUpTrack

        mediaPlayer.setDataSource(track.songUrl)
        mediaPlayer.prepareAsync()
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
        tvPlaybackTime = findViewById(R.id.tvPlaybackTime)
        bPlay = findViewById(R.id.bPlay)

        bPlay.setOnClickListener { if (!mediaPlayer.isPlaying) onMediaPlayerStart() else onMediaPlayerPause() }
        mediaPlayer.setOnCompletionListener { onMediaPlayerCompletion()  }

        updatePlaybackTimeRunnable = Runnable {
            if (mediaPlayer.isPlaying) {
                tvPlaybackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(updatePlaybackTimeRunnable, 400)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        handler.removeCallbacks(updatePlaybackTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            onMediaPlayerPause()
        }
    }


    private fun onMediaPlayerStart() {
        mediaPlayer.start()
        bPlay.setImageDrawable(pauseTrackImg)
        handler.post(updatePlaybackTimeRunnable)
    }
    private fun onMediaPlayerPause() {
        mediaPlayer.pause()
        bPlay.setImageDrawable(playTrackImg)
        handler.removeCallbacks(updatePlaybackTimeRunnable)
    }
    private fun onMediaPlayerCompletion() {
        bPlay.setImageDrawable(pauseTrackImg)
        handler.removeCallbacks(updatePlaybackTimeRunnable)
    }
}