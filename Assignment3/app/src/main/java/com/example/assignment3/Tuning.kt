package com.example.assignment3

data class Tuning(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageId: String = "",
    val notes: List<Note> = emptyList()
)

data class Note(
    val name: String = "",
    val freq: Double = 0.0,
    val soundId: String = ""
)
