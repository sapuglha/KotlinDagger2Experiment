package com.robotsandpencils.kotlindaggerexperiement.presentation.counter

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var count: MutableLiveData<Int> = MutableLiveData()
}