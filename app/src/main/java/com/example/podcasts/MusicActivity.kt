package com.example.podcasts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MusicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_music)
    }

    fun backOnClick(view: View) {
        finish()
    }
}