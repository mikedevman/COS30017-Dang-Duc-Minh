package com.example.assignment2

import android.util.Log

class Credits(var balance: Int) {fun borrow(amount: Int): Boolean {
    if (balance >= amount) {
        Log.d("Credits", "Deducting $amount from balance. New balance: ${balance - amount}.")
        balance -= amount
        return true
    }
    return false
}
}
