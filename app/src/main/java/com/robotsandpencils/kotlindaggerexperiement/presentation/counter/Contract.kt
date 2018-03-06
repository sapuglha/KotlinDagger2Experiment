package com.robotsandpencils.kotlindaggerexperiement.presentation.counter

/**
 * Contract
 */
interface Contract {

    /**
     * Presenter Contract
     */
    interface Presenter : com.robotsandpencils.kotlindaggerexperiement.presentation.base.Presenter<View>

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiement.presentation.base.View {
        fun getViewModel(): CounterViewModel?
    }
}