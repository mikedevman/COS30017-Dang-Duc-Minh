package com.example.assignment3

interface TuningDataSource {
    suspend fun getTunings(): List<Tuning>
}
