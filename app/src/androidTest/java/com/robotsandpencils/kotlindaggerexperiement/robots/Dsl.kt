package com.robotsandpencils.kotlindaggerexperiement.robots

fun app(func: AppRobot.() -> Unit) {
    val app = AppRobot()
    app.activityTestRule.launchActivity(null)
    app.apply { func() }
    app.activityTestRule.finishActivity()
}
