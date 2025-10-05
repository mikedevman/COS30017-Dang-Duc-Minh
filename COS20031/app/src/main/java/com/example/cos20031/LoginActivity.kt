package com.example.cos20031

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var txtUser: EditText
    private lateinit var txtPass: EditText
    private lateinit var loginButton: Button
    private lateinit var viewModel: SampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        txtUser = findViewById(R.id.username)
        txtPass = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginBtn)

        viewModel = ViewModelProvider(this)[SampleViewModel::class.java]
        observeViewModel()

        loginButton.setOnClickListener {
            val username = txtUser.text.toString()
            val password = txtPass.text.toString()

            if (username == "admin" && password == "123") {
                viewModel.incrementBadgeCount()
                val intent = Intent(this, AdminActivity::class.java)
                intent.putExtra("Username", username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.badgeCount.observe(this, androidx.lifecycle.Observer{
            showToast(it)
        })
    }

    private fun showToast(count: Int) {
        Toast.makeText(this, "Login attempts (ViewModel): $count", Toast.LENGTH_LONG).show()
    }
}
