package com.robotsandpencils.kotlindaggerexperiment.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity

class MainViewModel : ViewModel() {
    var users: MutableLiveData<List<UserEntity>> = MutableLiveData()
}