package com.robotsandpencils.kotlindaggerexperiement.presentation.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import timber.log.Timber

abstract class BaseFragment : Fragment() {

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
