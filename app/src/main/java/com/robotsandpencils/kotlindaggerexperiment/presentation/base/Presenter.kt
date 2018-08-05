package com.robotsandpencils.kotlindaggerexperiment.presentation.base

interface Presenter<in V : View> {
    fun attach(view: V)
    fun detach()
}
