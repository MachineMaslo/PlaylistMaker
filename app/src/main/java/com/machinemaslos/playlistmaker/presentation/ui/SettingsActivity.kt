package com.machinemaslos.playlistmaker.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.widget.SwitchCompat
import com.machinemaslos.playlistmaker.App
import com.machinemaslos.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setListeners()
    }

    private fun setListeners() {
        findViewById<ImageButton>(R.id.bReturn).setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.shareAppButton).setOnClickListener {
            startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer_course))
                type = "text/plain"
            }, null))
        }

        findViewById<ImageButton>(R.id.writeToSupportButton).setOnClickListener {
                startActivity(Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            })
        }

        findViewById<ImageButton>(R.id.termsOfUseButton).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.practicum_offer))
            })
        }

        val darkThemeSwitch = findViewById<SwitchCompat>(R.id.darkThemeSwitch)
        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(if (!checked) 1 else 2)
        }
        darkThemeSwitch.isChecked = (applicationContext as App).mode != 1
    }
}