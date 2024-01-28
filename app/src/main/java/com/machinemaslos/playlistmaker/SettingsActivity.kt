package com.machinemaslos.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setListeners()
    }

    private fun setListeners() {

        val shareAppIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer_course))
            type = "text/plain"
        }

        val writeToSupportIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
        }

        val termsOfUseIntent = Intent(Intent.ACTION_VIEW)
        termsOfUseIntent.data = Uri.parse(getString(R.string.practicum_offer))

        findViewById<ImageButton>(R.id.returnButton).setOnClickListener { onBackPressed() }
        findViewById<ImageButton>(R.id.shareAppButton).setOnClickListener { startActivity(Intent.createChooser(shareAppIntent, null)) }
        findViewById<ImageButton>(R.id.writeToSupportButton).setOnClickListener { startActivity(writeToSupportIntent) }
        findViewById<ImageButton>(R.id.termsOfUseButton).setOnClickListener { startActivity(termsOfUseIntent) }
        findViewById<SwitchCompat>(R.id.darkThemeSwitch).setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }
}