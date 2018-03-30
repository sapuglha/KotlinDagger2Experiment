package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity

class MainViewModel : ViewModel() {
    var users: MutableLiveData<List<UserEntity>> = MutableLiveData()
}