package com.robotsandpencils.kotlindaggerexperiement.app.managers

import android.app.AlarmManager
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.model.Environments
import timber.log.Timber


class EnvironmentManager(private val app: App,
                         private val environments: Environments,
                         private val preferencesManager: PreferencesManager) {
    fun selectEnvironment(activity: AppCompatActivity) {
        val adapter = MaterialSimpleListAdapter { dialog, index, _ ->
            val environment = environments.environments[index]
            preferencesManager.putString("selected_environment", environment.name)

            dialog.dismiss()

            doRestart(app)
        }

        val currentEnvironment = preferencesManager.getString("selected_environment", "Stub Server")

        environments.environments.forEach { environment ->
            val icon = if (currentEnvironment == environment.name)
                R.mipmap.ic_launcher_round
            else
                R.drawable.navigation_empty_icon

            adapter.add(MaterialSimpleListItem.Builder(activity)
                    .icon(icon)
                    .content(String.format("%s", environment.name))
                    .build())
        }

        MaterialDialog.Builder(activity)
                .title(R.string.environment_manager_choose_environment_title)
                .adapter(adapter, null)
                .show()
    }


    private fun doRestart(c: Context) =
            try {
                // fetch the package manager so we can get the default launch activity
                c.packageManager.let { pm ->
                    // create the intent with the default start activity for your application
                    pm.getLaunchIntentForPackage(c.packageName).let { startActivity ->
                        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        // create a pending intent so the application is restarted
                        // after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        val pendingIntentId = 223344
                        val pendingIntent = getActivity(c,
                                pendingIntentId,
                                startActivity,
                                FLAG_CANCEL_CURRENT)
                        val mgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)

                        // kill the application
                        System.exit(0)
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Was not able to restart application")
            }
}
