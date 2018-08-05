package com.robotsandpencils.kotlindaggerexperiment.presentation.comic

/**
 * Contract
 */
interface Contract {

    /**
     * Presenter Contract
     */
    interface Presenter : com.robotsandpencils.kotlindaggerexperiment.presentation.base.Presenter<View> {
        fun showPreviousComic()
        fun showNextComic()
    }

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiment.presentation.base.View {
        fun getViewModel(): ComicViewModel?
    }
}