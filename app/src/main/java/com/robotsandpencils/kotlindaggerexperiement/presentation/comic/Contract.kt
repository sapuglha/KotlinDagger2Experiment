package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

/**
 * Contract
 */
interface Contract {

    /**
     * Presenter Contract
     */
    interface Presenter : com.robotsandpencils.kotlindaggerexperiement.presentation.base.Presenter<View> {
        fun showPreviousComic()
        fun showNextComic()
    }

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiement.presentation.base.View {
        fun getViewModel(): ComicViewModel?
    }
}