package com.robotsandpencils.kotlindaggerexperiement.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    /* A helper method to return a viewmodel */
    inline fun <reified VM : ViewModel> safeGetViewModel(): VM? {
        return try {
            ViewModelProviders.of(this).get(VM::class.java)
        } catch (ex: IllegalStateException) {
            Timber.e(ex, "Unable to get viewModel at this lifecycle state.")
            null
        }
    }
}
