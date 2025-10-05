package com.example.cos20031

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminActivity : AppCompatActivity() {

    private lateinit var txtAdmin: TextView
    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("Username")

        if (username != null) {
            txtAdmin.text = "Hello, $username"
            Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()
        } else {
            txtAdmin.text = "Hello, Admin!"
            Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show()
        }

        btnExit.setOnClickListener {
            Log.d("AdminActivity", "Exit button clicked. Finishing AdminActivity.")
            finish()
        }
    }
}
