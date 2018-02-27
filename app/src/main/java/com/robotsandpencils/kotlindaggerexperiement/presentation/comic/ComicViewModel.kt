package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ComicViewModel : ViewModel() {

    val state: MutableLiveData<ComicState> = MutableLiveData()
/*
    val title: MutableLiveData<String> = MutableLiveData()
    val imageUrl: MutableLiveData<String> = MutableLiveData()
    val num: MutableLiveData<Int> = MutableLiveData()
    */
}