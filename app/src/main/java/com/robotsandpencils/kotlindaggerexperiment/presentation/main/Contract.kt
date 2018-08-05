package com.robotsandpencils.kotlindaggerexperiment.presentation.main

import androidx.annotation.StringRes
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity

/**
 * Main Contract
 */
interface Contract {

    /**
     * Presenter Contract
     */
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun addUser(id: String, firstName: String, lastName: String)
        fun removeUser(user: UserEntity)
        fun navigate(id: Int): Boolean
    }

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiment.presentation.base.View {
        fun getViewModel(): MainViewModel?
        fun setTitle(text: String)
        fun setTitle(@StringRes text: Int)
        fun clearFields()
        fun showError(message: String?)

        fun showHome()
        fun showDashboard()
        fun showNotifications()

        fun hideHome()
        fun hideDashboard()
        fun hideNotifications()
    }
}