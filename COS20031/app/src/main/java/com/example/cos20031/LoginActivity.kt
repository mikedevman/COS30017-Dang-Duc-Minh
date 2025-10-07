package com.example.cos20031

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var txtUser: EditText
    private lateinit var txtPass: EditText
    private lateinit var loginButton: Button

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val value = result.data?.getStringExtra("Test1")
                Toast.makeText(this, "Result from Admin: $value", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUser = findViewById(R.id.username)
        txtPass = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginBtn)

        loginButton.setOnClickListener {
            val username = txtUser.text.toString()
            val password = txtPass.text.toString()

            if (username == "admin" && password == "123") {
                val intent = Intent(this, AdminActivity::class.java)
                intent.putExtra("Username", username)
                getResult.launch(intent)
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
