package com.example.assignment2

import android.util.Log

// manages the user's credit balance
class Credits(var balance: Int) {

    // attempts to deduct a specified amount from the balance
    fun borrow(amount: Int): Boolean {
        // check if the user has enough credits
        if (balance >= amount) {
            Log.d("Credits", "Deducting $amount from balance. New balance: ${balance - amount}.")
            balance -= amount
            return true // return true if deduction was successful
        }
        return false // return false if funds are insufficient
    }
}
