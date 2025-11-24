package com.example.assignment3

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.*
import kotlinx.coroutines.launch
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import android.Manifest
import android.content.pm.PackageManager
import kotlin.math.abs
import android.media.MediaPlayer
import com.example.assignment3.TuningHelp
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: StringAdapter
    private lateinit var ui: TunerUIManager
    private lateinit var repo: TuningRepo
    private lateinit var title: TextView
    private lateinit var bigNote: TextView
    private lateinit var pitch: TextView
    private lateinit var cents: TextView
    private lateinit var tuningDesc: TextView
    private lateinit var rvStrings: RecyclerView
    private lateinit var tuningChipGroup: ChipGroup
    private lateinit var btnGuide: Button
    private val recorder = AudioRecord()
    private val PERMISSION_REQUEST_CODE = 1
    private val pitchDetector = PitchDetector()
    private val freqToNote = FreqToNote()
    private var lastDetectedFreq: Double = -1.0
    private var repetitionFreq = 0
    private var lastNoteData: NoteData? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repo = TuningRepo(this)
        title = findViewById(R.id.title)
        bigNote = findViewById(R.id.bigNote)
        pitch = findViewById(R.id.pitchVal)
        cents = findViewById(R.id.centsVal)
        tuningDesc = findViewById(R.id.tuningDesc)
        rvStrings = findViewById(R.id.rvStrings)
        tuningChipGroup = findViewById(R.id.accessoriesChipGroup)
        btnGuide = findViewById(R.id.btnGuide)

        adapter = StringAdapter(listOf())
        rvStrings.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvStrings.adapter = adapter
        adapter.onItemClick = { note, _ ->
            val currentTuning = ui.getCurrentTuning()
            val targetNote = currentTuning?.notes?.find { it.name == note.name }

            if (targetNote != null && targetNote.soundId.isNotBlank()) {
                playNoteAudio(targetNote.soundId)
            }

            val centsToTune = lastNoteData?.let {
                val centsDifference = it.cents
                when {
                    note.isTuned -> "In tune!"
                    centsDifference > 2 -> "Tune down by ${"%.1f".format(abs(centsDifference))} cents"
                    centsDifference < -2 -> "Tune up by ${"%.1f".format(abs(centsDifference))} cents"
                    else -> "In tune!"
                }
            }

            com.google.android.material.snackbar.Snackbar.make(rvStrings, "Correct Frequency: ${targetNote?.freq} Hz \n$centsToTune",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                .setAction("Okay"){}
                    .show()
        }

        ui = TunerUIManager(
            adapter,
            title,
            bigNote,
            pitch,
            cents,
            tuningDesc,
            rvStrings
        )

        btnGuide.setOnClickListener {
            val currentTuningId = ui.getCurrentTuning()?.id
            if (currentTuningId != null) {
                openTuningHelp(currentTuningId)
            }
        }
        setupUI()
        setupAudioRecorder()
    }

    private fun playNoteAudio(soundId: String) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        val audioId = resources.getIdentifier(soundId, "raw", packageName)
        if (audioId != 0) {
            try {
                mediaPlayer = MediaPlayer.create(this, audioId)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }

    private fun setupAudioRecorder() {
        recorder.context = this
        recorder.listener = object : AudioRecord.OnAudioFrameCaptured {
            override fun onFrameCaptured(frame: ShortArray) {
                val freq = pitchDetector.detectFrequency(frame, recorder.sampleRate)
                val noteData = freqToNote.detectNote(freq)

                lastNoteData = noteData

                runOnUiThread {
                    if (freq == 1764.0) { // no sound
                        println("Detected frequency: -")}
                    else if (freq > 0 && freq < 2000) {
                        println("Detected frequency: %.2f Hz".format(freq))
                    }

                    if (freq == lastDetectedFreq && freq > 0) {
                        repetitionFreq++
                    } else {
                        repetitionFreq = 0
                    }
                    lastDetectedFreq = freq

                    if (repetitionFreq >= 5) {
                        println("Restarting recorder...")
                        try {
                            recorder.stopRecording()
                            recorder.startRecording()
                        } catch(e: Exception) {
                            println("Error: $e")
                        }
                        repetitionFreq = 0
                    }

                    if (noteData != null && freq > 0 && freq < 2000) {
                        ui.updateDetectedPitch(noteData.name, freq, noteData.cents)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestMicPermission()
    }

    override fun onPause() {
        super.onPause()
        recorder.stopRecording()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun setupUI() {
        lifecycleScope.launch {
            val tunings = repo.getTunings()
            tuningChipGroup.removeAllViews()

            tunings.forEach { tuning ->
                val chip = Chip(this@MainActivity).apply {
                    text = tuning.name
                    isCheckable = true
                }
                tuningChipGroup.addView(chip)

                if (tuning == tunings.firstOrNull()) {
                    chip.isChecked = true
                    ui.loadTuning(tuning)
                }
            }
            tuningChipGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1) {
                    val selectedChip = group.findViewById<Chip>(checkedId)
                    val selectedTuning = tunings.find { it.name == selectedChip.text.toString() }
                    selectedTuning?.let {
                        ui.loadTuning(it)
                    }
                }
            }
        }
    }

    private fun requestMicPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_CODE)
        } else {
            recorder.startRecording()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recorder.startRecording()
            } else {
                println("Microphone permission denied")
            }
        }
    }

    fun openTuningHelp(selectedTuningId: String) {
        val intent = Intent(this, TuningHelp::class.java)
        intent.putExtra("tunings", selectedTuningId)
        startActivity(intent)
    }
}