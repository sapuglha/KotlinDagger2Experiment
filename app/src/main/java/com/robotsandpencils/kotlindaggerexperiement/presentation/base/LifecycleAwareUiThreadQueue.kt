package com.robotsandpencils.kotlindaggerexperiement.presentation.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import java.util.*

class LifecycleAwareUiThreadQueue(lifecycleOwner: LifecycleOwner) : UiThreadQueue(), LifecycleObserver {

    private val lifecycle: Lifecycle = lifecycleOwner.lifecycle

    private val deferredRunnables = ArrayList<Runnable>()

    private val deferredDelayedRunnable = ArrayList<DelayedRunnable>()

    private inner class DelayedRunnable internal constructor(internal val runnable: Runnable, internal val delayMilliseconds: Long)

    init {
        lifecycle.addObserver(this)
    }

    override fun run(runnable: Runnable) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            super.run(runnable)
        } else {
            Timber.i("Deferred running a runnable due to bad lifecycle state: %s", lifecycle.currentState.toString())
            synchronized(deferredRunnables) {
                deferredRunnables.add(runnable)
            }
        }
    }

    override fun runDelayed(runnable: Runnable, delayMilliseconds: Long) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            super.runDelayed(runnable, delayMilliseconds)
        } else {
            Timber.i("Deferred running a runnable due to bad lifecycle state: %s", lifecycle.currentState.toString())
            synchronized(deferredDelayedRunnable) {
                deferredDelayedRunnable.add(DelayedRunnable(runnable, delayMilliseconds))
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun processQueues() {
        while (!deferredRunnables.isEmpty()) {
            val runnable = synchronized(deferredRunnables) {
                deferredRunnables.removeAt(0)
            }
            run(runnable)
        }
        while (!deferredDelayedRunnable.isEmpty()) {
            val runnable = synchronized(deferredDelayedRunnable) {
                deferredDelayedRunnable.removeAt(0)
            }
            runDelayed(runnable.runnable, runnable.delayMilliseconds)
        }
    }
}
