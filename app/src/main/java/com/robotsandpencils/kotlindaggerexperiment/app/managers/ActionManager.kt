package com.robotsandpencils.kotlindaggerexperiment.app.managers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat.getColor
import android.view.View
import android.widget.Toast
import com.robotsandpencils.kotlindaggerexperiment.R
import timber.log.Timber

/**
 * Some shared actions that can be invoked
 */
class ActionManager {

    fun dialPhone(context: Context, number: String) {
        val phoneNumber = String.format("tel:%s", number)
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context.applicationContext, "Unable to dial", Toast.LENGTH_SHORT).show()
        }

    }

    fun launchWebLink(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
                .setToolbarColor(getColor(context, R.color.colorPrimary))
                .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
                .setShowTitle(true)
                .build()

        try {
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (ex: Throwable) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                context.startActivity(intent)
            } catch (ex2: Throwable) {
                Toast.makeText(context, "Unable to launch URL.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun launchApplication(context: Context, uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

    fun comingSoon(rootView: View) {
        showToast(rootView, R.string.action_manager_coming_soon)
    }

    fun sendEmailMessage(context: Context, emailAddress: String, subject: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))

        try {
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.action_manager_email_chooser_title)))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, R.string.action_manager_email_failed, Toast.LENGTH_SHORT).show()
        }

    }

    fun showToast(rootView: View, message: String) {
        Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    fun showToast(rootView: View, @StringRes id: Int) {
        Snackbar.make(rootView, id, BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    fun launchActivity(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Timber.e(ex)
        }
    }
}
