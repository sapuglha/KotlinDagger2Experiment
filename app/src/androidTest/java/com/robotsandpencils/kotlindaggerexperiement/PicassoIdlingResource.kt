@file:Suppress("PackageDirectoryMismatch")

package com.squareup.picasso

import android.app.Activity
import android.os.Handler
import android.support.test.espresso.IdlingResource
import android.support.test.runner.lifecycle.ActivityLifecycleCallback
import android.support.test.runner.lifecycle.Stage
import com.squareup.picasso.Picasso
import java.lang.ref.WeakReference

class PicassoIdlingResource : IdlingResource, ActivityLifecycleCallback {
    private var callback: IdlingResource.ResourceCallback? = null

    private var picassoWeakReference: WeakReference<Picasso>? = null

    companion object {
        private const val IDLE_POLL_DELAY_MILLIS = 100L
    }

    override fun getName(): String {
        return "PicassoIdlingResource"
    }

    override fun isIdleNow(): Boolean {
        return if (isIdle) {
            notifyDone()
            true
        } else {
            /* Force a re-check of the idle state in a little while.
             * If isIdleNow() returns false, Espresso only polls it every few seconds which can slow down our tests.
             */
            Handler().postDelayed({ isIdleNow }, IDLE_POLL_DELAY_MILLIS)

            false
        }
    }

    private val isIdle: Boolean
        get() = picassoWeakReference?.get()?.targetToAction?.isEmpty() ?: false


    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.callback = resourceCallback
    }

    private fun notifyDone() {
        if (callback != null) {
            callback!!.onTransitionToIdle()
        }
    }

    override fun onActivityLifecycleChanged(activity: Activity, stage: Stage) {
        when (stage) {
            Stage.CREATED -> picassoWeakReference = WeakReference(Picasso.with(activity))
            Stage.STOPPED ->
                // Clean up reference
                picassoWeakReference = null
            else -> {
                // NOP
            }
        }
    }
}