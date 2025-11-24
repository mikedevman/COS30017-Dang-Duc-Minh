package com.example.assignment3

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class TunerUIManager(
    private val adapter: StringAdapter,
    private val title: TextView,
    private val bigNote: TextView,
    private val pitch: TextView,
    private val cents: TextView,
    private val tuningDesc: TextView,
    private val rvStrings: RecyclerView
) {
    private var currentTuning: Tuning? = null
    private val tunedNotes = mutableSetOf<String>()
    private val CENTS_THRESHOLD = 5.0

    fun loadTuning(tuning: Tuning) {
        currentTuning = tuning
        tunedNotes.clear()
        tuningDesc.text = tuning.name
        val notesToDisplay = tuning.notes.map { NoteDisplay(name = it.name, isTuned = false) }
        adapter.updateNotes(notesToDisplay)
    }

    fun getCurrentTuning(): Tuning? {
        return currentTuning
    }

    fun updateDetectedPitch(noteName: String, frequency: Double, centsValue: Double) {
        bigNote.text = noteName
        pitch.text = if (frequency == 1764.0) "-" else "%.2f".format(frequency)
        cents.text = if (centsValue > 0) "+%.1f".format(centsValue) else "%.1f".format(centsValue)
        currentTuning?.let { tuning ->
            val isNoteInTune = abs(centsValue) < CENTS_THRESHOLD && tuning.notes.any { it.name == noteName }
            if (isNoteInTune) {
                tunedNotes.add(noteName)
            }

            val notesToDisplay = tuning.notes.map {
                NoteDisplay(name = it.name, isTuned = tunedNotes.contains(it.name))
            }
            adapter.updateNotes(notesToDisplay)
        }
    }
}