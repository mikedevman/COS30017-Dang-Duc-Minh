package com.example.assignment2

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Date
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.util.Log

// handles the final confirmation screen before a user borrows an item
class CheckoutActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set the correct layout based on device orientation
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.checkout_horizontal)
        } else {
            setContentView(R.layout.checkout_vertical)
        }

        // find all the necessary views in the layout
        val guitarImageView: ImageView = findViewById(R.id.guitarImage)
        val guitarNameTextView: TextView = findViewById(R.id.guitarNameText)
        val guitarDescriptionTextView: TextView = findViewById(R.id.guitarDescriptionText)
        val priceTextView: TextView = findViewById(R.id.priceText)
        val creditBalanceTextView: TextView = findViewById(R.id.creditBalanceText)
        val accessoriesChipGroup: ChipGroup = findViewById(R.id.accessoriesChipGroup)
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val confirmButton: Button = findViewById(R.id.confirmBorrowButton)
        val checkoutTitle: TextView = findViewById(R.id.checkoutTitle)

        // update ui colors based on the current region passed from mainactivity
        val currentRegion = intent.getStringExtra("SELECTED_REGION")
        if (currentRegion == "USA") {
            checkoutTitle.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4B0082"))
            confirmButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4B0082"))
        } else if (currentRegion == "UAE") {
            checkoutTitle.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3F51B5"))
            confirmButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3F51B5"))
        }

        // safely retrieve the guitar object from the intent extras
        val guitar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("GUITAR_EXTRA", Guitar::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Guitar>("GUITAR_EXTRA")
        }

        // retrieve user balance and create a temporary credits object for this screen
        val userBalance = intent.getIntExtra("USER_BALANCE_EXTRA", 0)
        val userCredits = Credits(userBalance)

        if (guitar != null) {
            // populate the ui with the guitar's details
            guitarImageView.setImageResource(guitar.imageResId)
            guitarNameTextView.text = guitar.name
            guitarDescriptionTextView.text = guitar.description
            priceTextView.text = "${guitar.price} Credits"
            creditBalanceTextView.text = "${userCredits.balance} Credits"

            // dynamically create and add chips for each accessory
            accessoriesChipGroup.removeAllViews()
            guitar.accessories.forEach { accessoryName ->
                val chip = Chip(this)
                chip.text = accessoryName
                accessoriesChipGroup.addView(chip)
            }

            // set listener for the confirm button
            confirmButton.setOnClickListener {
                // attempt to borrow the item using the credits object
                if (userCredits.borrow(guitar.price)) {
                    // on success, set a result and send the updated data back to mainactivity
                    Log.d(TAG, "Borrow successful for ${guitar.name}.")
                    Toast.makeText(this, "Borrow successful!", Toast.LENGTH_SHORT).show()

                    guitar.borrowedDate = Date().time

                    val resultIntent = Intent()
                    resultIntent.putExtra("UPDATED_GUITAR_EXTRA", guitar)
                    resultIntent.putExtra("NEW_BALANCE_EXTRA", userCredits.balance)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    // on failure, show an "insufficient funds" message
                    Log.w(TAG, "Borrow failed for ${guitar.name}. Insufficient credits.")
                    Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_LONG).show()
                }
            }
        }

        // set listener for the cancel button to close the activity
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
