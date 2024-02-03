package com.machinemaslos.playlistmaker.search_activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.machinemaslos.playlistmaker.R


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setListeners()
    }

    private fun setListeners() {
        searchEditText = findViewById(R.id.searchEditText)
        val cancelSearchButton = findViewById<ImageButton>(R.id.cancelSearch)
        val songsList = findViewById<RecyclerView>(R.id.songsList)

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

        cancelSearchButton.visibility = cancelSearchButtonVisibility(searchEditText.text)
        cancelSearchButton.setOnClickListener {
            searchEditText.text.clear()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        songsList.adapter = TracksAdapter(TRACKS)
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

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEFAULT_TEXT = ""

        private val TRACKS = listOf(
            Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"),
            Track("Can't Touch This", "MC Hammer", "4:17", "https://upload.wikimedia.org/wikipedia/en/d/d3/Please_Hammer_Don%27t_Hurt_%27Em.jpg"),
            Track("It's Tricky", "Run-DMC", "3:04", "https://upload.wikimedia.org/wikipedia/en/9/93/Raising_Hell_%28Run_DMC_album_-_cover_art%29.jpg")
        )
    }
}