package com.example.assignment3

import android.content.Context

class TuningRepo(private val source: TuningDataSource) {
    constructor(context: Context) : this (FirebaseDataSource(context))

    suspend fun getTunings(): List<Tuning> {
        return source.getTunings()
    }

    suspend fun getTuningById(id: String): Tuning? {
        return source.getTunings().find { it.id == id }
    }
}
