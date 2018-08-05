package com.robotsandpencils.kotlindaggerexperiment.presentation.counter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var count: MutableLiveData<Int> = MutableLiveData()
}