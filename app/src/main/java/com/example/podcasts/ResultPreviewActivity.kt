package com.example.podcasts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ResultPreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_preview)
    }

    fun onClick(view: View) {
        val intent = Intent(this, FinalActivity::class.java)
        startActivity(intent)
    }

    fun backOnClick(view: View) {
        finish()
    }
}