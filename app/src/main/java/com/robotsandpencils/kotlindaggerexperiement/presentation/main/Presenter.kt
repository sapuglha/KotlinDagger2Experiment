package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.util.Log
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.db.User
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/**
 * A super simple presenter
 */

class Presenter(private val mainRepository: MainRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    override fun attach(view: Contract.View) {
        super.attach(view)

        view.setTitle("Presenter Attached")

        val viewModel = view.getViewModel()
        viewModel?.users = mainRepository.getUserDao().getAll()
    }

    override fun addUser(id: String, firstName: String, lastName: String) {
        // Use Coroutines to rn this in the background and then do something on the UI
        // thread if successful.
        val deferred = async(CommonPool) {
            mainRepository.getUserDao().insertAll(User(id.toInt(), firstName, lastName))
            uiThreadQueue.run(Runnable {
                view?.setTitle("Record Added")
                view?.clearFields()
            })
        }

        // This will be called back when done, and if there is an error, throwable will be set
        deferred.invokeOnCompletion { throwable ->
            if (throwable != null) {
                Log.e("DB", "Unable to save: ${Thread.currentThread().name}", throwable)

                uiThreadQueue.run(Runnable {
                    view?.showError(throwable.message)
                })
            }
        }
    }

    override fun removeUser(user: User) {
        async(CommonPool) {
            mainRepository.getUserDao().delete(user)

            uiThreadQueue.run(Runnable {
                view?.setTitle("Record Deleted")
            })
        }
    }

    override fun navigate(id: Int): Boolean {
        when (id) {
            R.id.navigation_home -> {
                view?.setTitle(R.string.title_home)
                view?.showHome()
                view?.hideDashboard()
                view?.hideNotifications()
                return true
            }
            R.id.navigation_dashboard -> {
                view?.setTitle(R.string.title_dashboard)
                view?.hideHome()
                view?.showDashboard()
                view?.hideNotifications()
                return true
            }
            R.id.navigation_notifications -> {
                view?.setTitle(R.string.title_notifications)
                view?.hideHome()
                view?.hideDashboard()
                view?.showNotifications()
                return true
            }
        }
        return false
    }
}
