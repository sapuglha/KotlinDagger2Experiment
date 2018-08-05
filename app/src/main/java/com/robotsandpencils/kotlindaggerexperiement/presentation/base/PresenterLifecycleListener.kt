package com.robotsandpencils.kotlindaggerexperiement.presentation.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class PresenterLifecycleListener<V : View, out P : Presenter<V>>(
        val lifecycle: Lifecycle,
        val view: V,
        val presenter: P,
        val attachInCreate: Boolean = false,
        private val onStarted: () -> Unit = {}) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun started() {
        if (!attachInCreate) {
            presenter.attach(view)
        }

        // Call the method when the lifecycle is in the started state only
        onStarted()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopped() {
        if (!attachInCreate) {
            presenter.detach()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun created() {
        if (attachInCreate) {
            presenter.attach(view)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyed() {
        if (attachInCreate) {
            presenter.detach()
        }
    }
}
