package com.example.cos20031

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData


class SampleViewModel : ViewModel() {

    private val _count = MutableLiveData<Int>()
    var number = 0

    val badgeCount: LiveData<Int>
        get() = _count

    fun incrementBadgeCount(){
        _count.postValue(number++)
    }

    override fun onCleared() {
        super.onCleared()
    }
}