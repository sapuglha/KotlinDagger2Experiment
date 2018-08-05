package com.robotsandpencils.kotlindaggerexperiment.presentation.comic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ComicViewModel : ViewModel() {
    val state: MutableLiveData<ComicState> = MutableLiveData()
}