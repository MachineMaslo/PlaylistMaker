package com.machinemaslos.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.machinemaslos.playlistmaker.EX_TRACK
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.data.web.LookUpTrack
import com.machinemaslos.playlistmaker.data.web.Track
import com.machinemaslos.playlistmaker.presentation.presenters.HistoryInteractor
import com.machinemaslos.playlistmaker.presentation.presenters.SearchInteractor


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView

    var searchText: String = ""

    private val searchInteractor = SearchInteractor(this)
    private lateinit var historyInteractor: HistoryInteractor

    private val tracks = mutableListOf<Track>()

    private val connectionProblemsDrawable: Drawable by lazy { getDrawable(R.drawable.connection_problems)!! }
    private val nothingFoundDrawable: Drawable by lazy { getDrawable(R.drawable.nothing_found)!! }

    private val errorHolder: LinearLayout by lazy { findViewById(R.id.errorHolder) }
    private val errorTitle: TextView by lazy { findViewById(R.id.errorTitle) }
    private val errorSubtitle: TextView by lazy { findViewById(R.id.errorSubtitle) }
    private val errorPicture: ImageView by lazy { findViewById(R.id.errorPicture) }
    private val updateButton: Button by lazy { findViewById(R.id.updateButton) }

    private lateinit var youSearchedTitle: TextView
    private lateinit var clearHistoryButton: Button

    private val pbLoading by lazy { findViewById<ProgressBar>(R.id.pbLoading) }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { if (searchText.isNotEmpty()) search() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        historyInteractor = HistoryInteractor(this@SearchActivity)

        searchEditText = findViewById(R.id.etSearch)
        tracksRecyclerView = findViewById(R.id.rvTracks)

        youSearchedTitle = findViewById(R.id.tvYouSearched)
        clearHistoryButton = findViewById(R.id.bClearHistory)

        setListeners()
    }

    private fun setListeners() {
        val cancelSearchButton = findViewById<ImageButton>(R.id.bCancelSearch)

        findViewById<ImageButton>(R.id.bReturn).setOnClickListener { finish() }

        searchEditText.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(searchRunnable)
                cancelSearchButton.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    showTracksOrHistory(SW_HISTORY)
                }
                else {
                    showTracksOrHistory(SW_TRACKS)

                    handler.removeCallbacks(searchRunnable)
                    handler.postDelayed(searchRunnable, RUNNABLE_DELAY)
                }
            }

            override fun afterTextChanged(s: Editable?) { searchText = s.toString() }

        })
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                searchEditText.clearFocus()
                handler.removeCallbacks(searchRunnable)

                showTracksOrHistory(SW_TRACKS)
                if (searchText.isNotEmpty()) search()
            }
            true
        }
        searchEditText.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus and searchText.isEmpty()) {
                showTracksOrHistory(SW_HISTORY)
            }
        }

        cancelSearchButton.setOnClickListener {
            hideKeyboard()

            searchEditText.clearFocus()
            searchEditText.text.clear()
            tracks.clear()
            showTracksOrHistory(SW_NOTHING)
        }

        updateButton.setOnClickListener { search() }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            searchEditText.clearFocus()
            showTracksOrHistory(SW_NOTHING)
        }
    }

    private fun search() {
        setTracksLoadingState(true)
        searchInteractor.search()
    }

    //InstanceState
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT, DEFAULT_TEXT))
    }

    // Tracks
    fun setTracksLoadingState(isLoading: Boolean) {
        if (isLoading) {
            showTracksOrHistory(SW_NOTHING)
            pbLoading.visibility = View.VISIBLE
        }
        else {
            showTracksOrHistory(SW_TRACKS)
            pbLoading.visibility = View.GONE
        }
    }
    fun setTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyTrackListChanged() {
        tracksRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun goToPlayer(track: LookUpTrack) {
        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
            .putExtra(EX_TRACK, track)
        startActivity(playerIntent)
    }

    // Errors
    private fun showError(title: String, subtitle: String, picture: Drawable, showUpdateButton: Boolean) {
        searchEditText.clearFocus()
        errorHolder.isVisible = true
        errorTitle.text = title
        errorSubtitle.text = subtitle
        errorPicture.setImageDrawable(picture)
        if (showUpdateButton) updateButton.visibility = View.VISIBLE else updateButton.visibility = View.GONE
    }

    private fun hideError() {
        errorHolder.isVisible = false
    }

    fun showInternetConnectionError() {
        showError(getString(R.string.internet_connection_error_title), getString(R.string.internet_connection_error_subtitle), connectionProblemsDrawable, true)
    }

    fun showNothingFoundError() {
        showError(getString(R.string.nothing_found_error_title), "", nothingFoundDrawable, false)
    }


    /**
     * SW_NOTHING - Nothing,
     * SW_TRACKS - Tracks,
     * SW_HISTORY - History
     */
    fun showTracksOrHistory(show: Int) {
        hideError()

        when (show) {

            SW_NOTHING -> {

                youSearchedTitle.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE

                tracksRecyclerView.adapter = null

            }

            SW_TRACKS -> {

                youSearchedTitle.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE

                tracksRecyclerView.adapter = TracksAdapter(tracks, searchInteractor, historyInteractor)

            }

            SW_HISTORY -> {

                val history = historyInteractor.getHistory()
                if (history.isEmpty()) return

                youSearchedTitle.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE

                tracksRecyclerView.adapter = TracksAdapter(history, searchInteractor, historyInteractor)

            }

        }

    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }


    companion object {

        private const val SW_NOTHING = 0
        private const val SW_TRACKS = 1
        private const val SW_HISTORY = 2

        private const val RUNNABLE_DELAY = 2000L

        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEFAULT_TEXT = ""
    }
}