package com.example.podcasts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        edit_ticks.clipToOutline = true
        edit_waves_overlay.clipToOutline = true
    }

    fun buttonOnClick(view: View) {
        val intent = Intent(this, UploadActivity::class.java)
        startActivity(intent)
    }


    fun toMusic(view: View) {
        val intent = Intent(this, MusicActivity::class.java)
        startActivity(intent)
    }

    fun backOnClick(view: View) {
        finish()
    }
}