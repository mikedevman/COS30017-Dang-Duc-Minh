package com.example.assignment01

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {
    private var score = 0
    private var currentHold = 0
    private var hasFallen = false
    private var sessionActive = false
    private var isEnglish = true

    private lateinit var titleText: TextView
    private lateinit var tileScore: TextView
    private lateinit var scoreText: TextView
    private lateinit var holdText: TextView
    private lateinit var holdTitle: TextView
    private lateinit var climbBtn: Button
    private lateinit var fallBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var startStopBtn: Button
    private lateinit var langLabel: TextView
    private lateinit var switchLangBtn: ImageView
    private lateinit var langSwitcher: SwitchLang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        setContentView(if (isLandscape) R.layout.landscape_main else R.layout.portrait_main)
        val rootView = findViewById<ViewGroup>(if (isLandscape) R.id.main_landscape else R.id.main_portrait)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        titleText = findViewById(R.id.titleText)
        tileScore = findViewById(R.id.tileScore)
        scoreText = findViewById(R.id.scoreText)
        holdText = findViewById(R.id.holdText)
        holdTitle = findViewById(R.id.holdTitle)
        climbBtn = findViewById(R.id.climbBtn)
        fallBtn = findViewById(R.id.fallBtn)
        resetBtn = findViewById(R.id.resetBtn)
        startStopBtn = findViewById(R.id.startStopBtn)
        langLabel = findViewById(R.id.langLabel)
        switchLangBtn = findViewById(R.id.switchLangBtn)

        langSwitcher = SwitchLang(
            titleText,
            tileScore,
            holdTitle,
            climbBtn,
            fallBtn,
            resetBtn,
            startStopBtn,
            langLabel,
            switchLangBtn
        )

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score")
            currentHold = savedInstanceState.getInt("currentHold")
            hasFallen = savedInstanceState.getBoolean("hasFallen")
            sessionActive = savedInstanceState.getBoolean("sessionActive")
            isEnglish = savedInstanceState.getBoolean("isEnglish")
            Log.d("MainActivity", "State restored: score=$score, hold=$currentHold, sessionActive=$sessionActive, isEnglish=$isEnglish")
        }

        updateScoreDisplay()
        updateButtonState()
        langSwitcher.update(isEnglish, sessionActive)

        climbBtn.setOnClickListener { climb() }
        fallBtn.setOnClickListener { fall() }
        resetBtn.setOnClickListener { reset() }
        startStopBtn.setOnClickListener { toggleSession() }
        switchLangBtn.setOnClickListener { toggleLanguage() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("currentHold", currentHold)
        outState.putBoolean("hasFallen", hasFallen)
        outState.putBoolean("sessionActive", sessionActive)
        outState.putBoolean("isEnglish", isEnglish)
        Log.d("MainActivity", "State saved: score=$score, hold=$currentHold, sessionActive=$sessionActive, isEnglish=$isEnglish")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        score = savedInstanceState.getInt("score")
        currentHold = savedInstanceState.getInt("currentHold")
        hasFallen = savedInstanceState.getBoolean("hasFallen")
        sessionActive = savedInstanceState.getBoolean("sessionActive")
        isEnglish = savedInstanceState.getBoolean("isEnglish")
        updateScoreDisplay()
        updateButtonState()
        langSwitcher.update(isEnglish, sessionActive)
        Log.d("MainActivity", "State restored via onRestoreInstanceState: score=$score, hold=$currentHold, sessionActive=$sessionActive, isEnglish=$isEnglish")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("MainActivity", "Switched to landscape mode")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("MainActivity", "Switched to portrait mode")
        }
    }

    private fun toggleLanguage() {
        isEnglish = !isEnglish
        langSwitcher.update(isEnglish, sessionActive)
        Log.d("MainActivity", "Language changed. isEnglish=$isEnglish")
    }

    private fun toggleSession() {
        sessionActive = !sessionActive
        if (sessionActive) reset()
        updateButtonState()
        langSwitcher.update(isEnglish, sessionActive)
        Log.d("MainActivity", "Session toggled. Active: $sessionActive")
    }

    private fun updateButtonState() {
        climbBtn.isEnabled = sessionActive
        fallBtn.isEnabled = sessionActive
        resetBtn.isEnabled = sessionActive
    }

    private fun climb() {
        if (!sessionActive || hasFallen || currentHold >= 9) {
            Log.d("MainActivity", "Climb ignored. sessionActive=$sessionActive, hasFallen=$hasFallen, currentHold=$currentHold")
            return
        }
        currentHold++
        when (currentHold) {
            in 1..3 -> score += 1
            in 4..6 -> score += 2
            in 7..9 -> score += 3
        }
        if (score > 18) score = 18
        updateScoreDisplay()
        Log.d("MainActivity", "Climbed to hold $currentHold. Score: $score")
    }

    private fun fall() {
        if (!sessionActive || currentHold == 0 || hasFallen || currentHold == 9) {
            Log.d("MainActivity", "Fall ignored. sessionActive=$sessionActive, currentHold=$currentHold, hasFallen=$hasFallen")
            return
        }
        score -= 3
        if (score < 0) score = 0
        hasFallen = true
        updateScoreDisplay()
        Log.d("MainActivity", "Fell from hold $currentHold. New score: $score")
    }

    private fun reset() {
        score = 0
        currentHold = 0
        hasFallen = false
        updateScoreDisplay()
        Log.d("MainActivity", "Score reset")
    }

    private fun updateScoreDisplay() {
        scoreText.text = score.toString()
        holdText.text = currentHold.toString()
        val color = when (currentHold) {
            in 1..3 -> Color.BLUE
            in 4..6 -> Color.GREEN
            in 7..9 -> Color.RED
            else -> Color.WHITE
        }
        scoreText.setTextColor(color)
        holdText.setTextColor(color)
    }
}
