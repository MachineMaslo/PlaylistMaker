package com.machinemaslos.playlistmaker.search_activity

import android.graphics.drawable.Drawable
import android.os.Bundle
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.machinemaslos.playlistmaker.R
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

    private val searchService = retrofit.create(SearchApi::class.java)

    private val tracks = mutableListOf<Track>()

    private lateinit var connectionProblemsDrawable: Drawable
    private lateinit var nothingFoundDrawable: Drawable

    private lateinit var errorHolder: LinearLayout
    private lateinit var errorTitle: TextView
    private lateinit var errorSubtitle: TextView
    private lateinit var errorPicture: ImageView
    private lateinit var updateButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.searchEditText)
        tracksRecyclerView = findViewById(R.id.songsList)

        connectionProblemsDrawable = getDrawable(R.drawable.connection_problems)!!
        nothingFoundDrawable = getDrawable(R.drawable.nothing_found)!!

        errorHolder = findViewById(R.id.errorHolder)
        errorTitle = findViewById(R.id.errorTitle)
        errorSubtitle = findViewById(R.id.errorSubtitle)
        errorPicture = findViewById(R.id.errorPicture)
        updateButton = findViewById(R.id.updateButton)

        setListeners()
    }

    private fun setListeners() {
        val cancelSearchButton = findViewById<ImageButton>(R.id.cancelSearch)
        val tracksRecyclerView = findViewById<RecyclerView>(R.id.songsList)

        findViewById<ImageButton>(R.id.returnButton).setOnClickListener { finish() }

        searchEditText.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelSearchButton.visibility = cancelSearchButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }

        })
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            true
        }

        cancelSearchButton.visibility = cancelSearchButtonVisibility(searchEditText.text)
        cancelSearchButton.setOnClickListener {
            searchEditText.text.clear()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            tracks.clear()
            tracksRecyclerView.adapter?.notifyDataSetChanged()
        }

        errorHolder.visibility = View.GONE

        tracksRecyclerView.adapter = TracksAdapter(tracks)

        updateButton.setOnClickListener {
            search()
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
    private fun cancelSearchButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        errorHolder.visibility = View.GONE
        tracks.clear()
        searchService.search(searchEditText.text.toString()).enqueue(object: Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {

                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                    }
                    else {
                        showError("Ничего не нашлось", "", nothingFoundDrawable, false)
                    }
                }
                else {
                    showError("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету", connectionProblemsDrawable, true)
                }
                tracksRecyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showError("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету", connectionProblemsDrawable, true)
                tracksRecyclerView.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun showError(title: String, subtitle: String, picture: Drawable, showUpdateButton: Boolean) {
        errorHolder.visibility = View.VISIBLE
        errorTitle.text = title
        errorSubtitle.text = subtitle
        errorPicture.setImageDrawable(picture)
        if (showUpdateButton) updateButton.visibility = View.VISIBLE else updateButton.visibility = View.GONE
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEFAULT_TEXT = ""
    }
}