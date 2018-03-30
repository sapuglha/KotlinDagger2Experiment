package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.util.Log
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.common.AsyncTransformer
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import com.robotsandpencils.kotlinexperiment.domain.usecases.ManageUsers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/**
 * A super simple presenter
 */

class Presenter(repository: UserRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    private val manageUsers = ManageUsers(AsyncTransformer(), repository)

    override fun attach(view: Contract.View) {
        super.attach(view)

        view.setTitle("Presenter Attached")

        manageUsers.getUsers().subscribe {
            view.getViewModel()?.apply {
                users.postValue(it)
            }
        }
    }

    override fun addUser(id: String, firstName: String, lastName: String) {
        // Use Coroutines to rn this in the background and then do something on the UI
        // thread if successful.
        val deferred = async(CommonPool) {
            manageUsers.addUsers(UserEntity(id.toInt(), firstName, lastName))
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

    override fun removeUser(user: UserEntity) {
        async(CommonPool) {
            manageUsers.deleteUser(user)

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
