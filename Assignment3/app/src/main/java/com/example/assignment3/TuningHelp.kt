package com.example.assignment3

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class TuningHelp : AppCompatActivity() {
    private lateinit var repo: TuningRepo
    private lateinit var instrumentImage: ImageView
    private lateinit var instrumentInfo: TextView
    private lateinit var okButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tuning_help)

        repo = TuningRepo(this)

        instrumentImage = findViewById(R.id.instrumentImage)
        instrumentInfo = findViewById(R.id.instrumentInfo)
        okButton = findViewById(R.id.ok)

        okButton.setOnClickListener {
            finish()
        }

        val tuningId = intent.getStringExtra("tunings")
        if (tuningId != null) {
            loadTuning(tuningId)
        }
    }

    private fun loadTuning(tuningId: String) {
        lifecycleScope.launch {
            val tuning = repo.getTuningById(tuningId)

            if (tuning != null) {
                instrumentInfo.text = tuning.description
                val imageId = resources.getIdentifier(tuning.imageId, "drawable", packageName) // <-- RESOLVED
                if (imageId != 0) {
                    instrumentImage.setImageResource(imageId)
                }
            }
        }
    }
}