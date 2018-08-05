package com.robotsandpencils.kotlindaggerexperiment.presentation.counter

/**
 * Contract
 */
interface Contract {

    /**
     * Presenter Contract
     */
    interface Presenter : com.robotsandpencils.kotlindaggerexperiment.presentation.base.Presenter<View>

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiment.presentation.base.View {
        fun getViewModel(): CounterViewModel?
    }
}