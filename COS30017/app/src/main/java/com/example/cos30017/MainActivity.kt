package com.example.cos30017

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdgeimport androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    // 1. Declare variables as class properties
    private lateinit var txtInfor: EditText
    private lateinit var writeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtInfor = findViewById(R.id.txtInfor)
        writeButton = findViewById(R.id.write)

        writeButton.setOnClickListener {
            save()
        }
    }

    // 2. Move the save() function outside of onCreate()
    private fun save() {
        val fileName = "test.txt"
        val data = txtInfor.text.toString()

        if (data.isEmpty()) {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            return
        }

        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = openFileOutput(fileName, MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
            Toast.makeText(this, "Saved to $fileName", Toast.LENGTH_LONG).show()
            txtInfor.text.clear()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving file", Toast.LENGTH_LONG).show()
        } finally {
            fileOutputStream?.close()
        }
    }
}
