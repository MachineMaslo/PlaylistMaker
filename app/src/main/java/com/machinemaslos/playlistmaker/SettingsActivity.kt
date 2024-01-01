package com.machinemaslos.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_screen)

        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}