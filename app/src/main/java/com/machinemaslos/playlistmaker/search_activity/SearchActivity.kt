package com.machinemaslos.playlistmaker.search_activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
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
import com.machinemaslos.playlistmaker.PlayerActivity
import com.machinemaslos.playlistmaker.R
import com.machinemaslos.playlistmaker.SEARCH_HISTORY
import com.machinemaslos.playlistmaker.SEARCH_HISTORY_DEFAULT
import com.machinemaslos.playlistmaker.SHARED_PREFS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView

    private var searchText = ""

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var historySaver: HistorySaver

    private val searchService = retrofit.create(SearchApi::class.java)

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
    private val searchRunnable = Runnable {
        searchText.let {
            if (it.isNotEmpty()) {
                search()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        historySaver = HistorySaver(this@SearchActivity)

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(searchRunnable)
                cancelSearchButton.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    showTracksOrHistory(2)
                } else {
                    showTracksOrHistory(1)
                    handler.removeCallbacks(searchRunnable)
                    handler.postDelayed(searchRunnable, 2000)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }

        })
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                searchEditText.clearFocus()

                search()
            }
            true
        }
        searchEditText.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus and searchEditText.text.isEmpty()) {
                showTracksOrHistory(2)
            }
            else {
                showTracksOrHistory(1)
            }
        }

        showTracksOrHistory(1)

        cancelSearchButton.setOnClickListener {
            hideKeyboard()

            searchEditText.clearFocus()
            searchEditText.text.clear()
            tracks.clear()
            showTracksOrHistory(1)
        }
        updateButton.setOnClickListener {
            search()
        }
        clearHistoryButton.setOnClickListener {
            sharedPrefs.edit().putString( SEARCH_HISTORY, SEARCH_HISTORY_DEFAULT).apply()
            searchEditText.clearFocus()
            showTracksOrHistory(1)
        }
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


    //SearchEditText

    private fun search() {
        if (searchEditText.text.isNotEmpty()) {
            pbLoading.visibility = View.VISIBLE  // Show the progress bar when the search starts
            showTracksOrHistory(1)
            searchEditText.clearFocus()
            tracks.clear()

            searchService.search(searchEditText.text.toString()).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                        } else {
                            showError("Ничего не нашлось", "", nothingFoundDrawable, false)
                        }
                    } else {
                        showError("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету", connectionProblemsDrawable, true)
                    }
                    tracksRecyclerView.adapter?.notifyDataSetChanged()
                    pbLoading.visibility = View.GONE
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showError("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету", connectionProblemsDrawable, true)
                    tracksRecyclerView.adapter?.notifyDataSetChanged()
                    pbLoading.visibility = View.GONE
                }
            })
        }
    }

    fun lookUpSearch(trackId: String) {
        searchService.lookUpSearch(trackId).enqueue(object: Callback<TrackLookUpResponse> {
            override fun onResponse(call: Call<TrackLookUpResponse>, response: Response<TrackLookUpResponse>) {
                if (response.code() == 200) {
                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java).putExtra(EX_TRACK, response.body()?.results?.firstOrNull())
                    startActivity(playerIntent)
                }
            }

            override fun onFailure(call: Call<TrackLookUpResponse>, t: Throwable) {
            }
        })
    }


    private fun showError(title: String, subtitle: String, picture: Drawable, showUpdateButton: Boolean) {
        searchEditText.clearFocus()
        errorHolder.isVisible = true
        errorTitle.text = title
        errorSubtitle.text = subtitle
        errorPicture.setImageDrawable(picture)
        if (showUpdateButton) updateButton.visibility = View.VISIBLE else updateButton.visibility = View.GONE
    }

    /**
     * 1 - Tracks,
     * 2 - History
     */
    private fun showTracksOrHistory(show: Int) {
        errorHolder.visibility = View.GONE

        if (show == 1) {
            youSearchedTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE

            tracksRecyclerView.adapter = TracksAdapter(tracks)
        }
        else if (show == 2) {
            val history = historySaver.getHistory()

            if (history.isNotEmpty()) {
                youSearchedTitle.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE

                tracksRecyclerView.adapter = TracksAdapter(history)
            }
            else {
                showTracksOrHistory(1)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }


    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEFAULT_TEXT = ""
    }
}