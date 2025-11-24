package com.example.assignment3

import kotlin.math.roundToInt

data class NoteData(val name: String, val cents: Double)

class FreqToNote {
    private val notes = arrayOf( "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    fun detectNote(freq: Double): NoteData? {
        // use A4 as reference note
        // A4 = 440.0 Hz
        val semitone = 12 * (Math.log(freq / 440.0) / Math.log(2.0)) // log2(x) = ln(x) / ln(2)
        // A4 midi = 69
        val midi = (69 + semitone).roundToInt()
        val noteName = notes[midi % 12] // midi % 12 is the index of the note in the notes array
        val octave = (midi / 12) - 1 // subtract 1 because midi 0 = C-1
        val nearestNote = 440.0 * Math.pow(2.0, (midi - 69) / 12.0) // power of 2 because every octave doubles the frequency
        // 1 semitone = 100 cents -> 12 semitone = 1200 cents
        // positive -> sharp
        // negative -> flat
        // zero -> in tune
        val cents = 1200 * Math.log(freq/ nearestNote) / Math.log(2.0)
        return NoteData("$noteName$octave", cents)
    }

}