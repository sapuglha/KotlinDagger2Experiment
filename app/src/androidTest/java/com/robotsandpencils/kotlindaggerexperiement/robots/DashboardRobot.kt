package com.robotsandpencils.kotlindaggerexperiement.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.robotsandpencils.kotlindaggerexperiement.R
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf


class DashboardRobot : BaseRobot() {

    fun assertTitle(title: String) {
        assertText(R.id.titleText, title)
    }

    fun assertImageUrl(url: String) {
        onView(withId(R.id.imageView)).check(matches(allOf(isDisplayed(), withTagValue(`is`(url)))))
    }

    fun tapPreviousComic() {
        clickButton(R.id.previousButton)
    }
}