package com.example.cos30017

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    lateinit var txtDisplay: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtDisplay = findViewById(R.id.txtDisplay)
        callThread()
    }

    suspend fun Thread1(): String {
        Log.d("T1", "Start Thread 1")
        delay(1700)
        Log.d("T1", "finish Thread 1")
        return "Thread 1"
    }

    suspend fun Thread2(): String {
        Log.d("T2", "Start Thread 2")
        delay(1000)
        Log.d("T2", "finish Thread 2")
        return "Thread 2"
    }

    fun callThread() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val job1: Deferred<String> = async {
                    Thread1()
                }
                val job2: Deferred<String> = async {
                    Thread2()
                }
                val result1 = job1.await()
                val result2 = job2.await()
                val result = "$result1 + $result2"

                Log.d("T1", "Result: $result")

                withContext(Main) {
                    txtDisplay.text = "Result: $result"
                }
            }
            Log.d("T1", "Execute time: ${executionTime}ms")
            withContext(Main) {
                txtDisplay.append("\nExecution time: ${executionTime}ms")
            }
        }
    }
}
