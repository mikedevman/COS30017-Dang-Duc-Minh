package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var inputA: EditText
    private lateinit var inputB: EditText
    private lateinit var plusButton: Button
    private lateinit var minusButton: Button
    private lateinit var multiplyButton: Button
    private lateinit var divideButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputA = findViewById(R.id.input_a)
        inputB = findViewById(R.id.input_b)
        plusButton = findViewById(R.id.plus)
        minusButton = findViewById(R.id.minus)
        multiplyButton = findViewById(R.id.multiply)
        divideButton = findViewById(R.id.subtract)
        resultTextView = findViewById(R.id.result)

        fun getNumbers(): Pair<Double, Double>? {
            val aText = inputA.text.toString()
            val bText = inputB.text.toString()

            if (aText.isEmpty() || bText.isEmpty()) {
                resultTextView.text = "Error: Please enter both numbers."
                return null
            }

            return try {
                val numA = aText.toDouble()
                val numB = bText.toDouble()
                Pair(numA, numB)
            } catch (e: NumberFormatException) {
                resultTextView.text = "Error: Invalid input. Please enter valid numbers."
                null
            }
        }

        plusButton.setOnClickListener {
            getNumbers()?.let { (numA, numB) ->
                resultTextView.text = "Result: ${numA + numB}"
            }
        }

        minusButton.setOnClickListener {
            getNumbers()?.let { (numA, numB) ->
                resultTextView.text = "Result: ${numA - numB}"
            }
        }

        multiplyButton.setOnClickListener {
            getNumbers()?.let { (numA, numB) ->
                resultTextView.text = "Result: ${numA * numB}"
            }
        }

        divideButton.setOnClickListener {
            getNumbers()?.let { (numA, numB) ->
                if (numB == 0.0) {
                    resultTextView.text = "Error: Cannot divide by zero."
                } else {
                    resultTextView.text = "Result: ${numA / numB}"
                }
            }
        }
    }
}
