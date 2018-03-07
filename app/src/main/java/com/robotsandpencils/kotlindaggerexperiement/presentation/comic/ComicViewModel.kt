package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ComicViewModel : ViewModel() {
    val state: MutableLiveData<ComicState> = MutableLiveData()
}