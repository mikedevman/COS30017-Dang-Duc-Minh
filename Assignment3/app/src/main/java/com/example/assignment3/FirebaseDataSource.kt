package com.example.assignment3

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class FirebaseDataSource(private val context: Context) : TuningDataSource {
    private val db = Firebase.firestore
    private val collectionName = "tunings"

    override suspend fun getTunings(): List<Tuning> {
        return try {
            val snapshot = db.collection(collectionName).get().await()
            val tunings = snapshot.documents.mapNotNull { document ->
                document.toObject(Tuning::class.java)?.copy(id = document.id)
            }
                tunings
        } catch (e: Exception) {
            println("Error getting tunings: $e")
            emptyList()
        }
    }
}