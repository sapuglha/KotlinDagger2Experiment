package com.robotsandpencils.kotlindaggerexperiement.robots

import android.os.SystemClock
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.presentation.main.MainActivity
import org.junit.Rule

open class AppRobot : BaseRobot() {

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    fun home(func: HomeRobot.() -> Unit) {
        clickTab(R.id.navigation_home)
        HomeRobot().apply { func() }
    }

    fun dashboard(func: DashboardRobot.() -> Unit) {
        clickTab(R.id.navigation_dashboard)
        DashboardRobot().apply { func() }
    }

    fun notifications(func: NotificationsRobot.() -> Unit) {
        clickTab(R.id.navigation_notifications)
        NotificationsRobot().apply { func() }
    }

    private fun clickTab(id: Int) {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
    }

    /** Uses constants from ActivityInfo class
     * @param rotation either ActivityInfo.SCREEN_ORIENTATION_PORTRAIT or ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
     */
    fun rotateDevice(rotation: Int) {
        activityTestRule.activity.requestedOrientation = rotation
        SystemClock.sleep(2000)
    }
}
