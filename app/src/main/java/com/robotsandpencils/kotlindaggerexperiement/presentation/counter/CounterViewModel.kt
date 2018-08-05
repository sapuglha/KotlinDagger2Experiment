package com.robotsandpencils.kotlindaggerexperiement.presentation.counter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var count: MutableLiveData<Int> = MutableLiveData()
}