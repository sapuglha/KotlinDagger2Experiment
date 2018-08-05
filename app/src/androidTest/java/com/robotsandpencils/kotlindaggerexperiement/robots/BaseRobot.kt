package com.robotsandpencils.kotlindaggerexperiement.robots

import android.os.SystemClock
import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.uiautomator.UiDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.robotsandpencils.kotlindaggerexperiement.R
import org.hamcrest.*

abstract class BaseRobot {

    fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent

                return (parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position))
            }
        }
    }

    fun assertText(@IdRes id: Int, message: String): ViewInteraction =
            Espresso.onView(CoreMatchers.allOf(ViewMatchers.withId(id), ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText(message)))


    fun componentInList(component: Matcher<View>, list: Matcher<View>, index: Int): ViewInteraction {
        return Espresso.onView(Matchers.allOf(component, ViewMatchers.isDescendantOfA(childAtPosition(list, index))))
    }

    fun componentInTagged(component: Matcher<View>, tagValue: Matcher<Any>): ViewInteraction {
        return Espresso.onView(Matchers.allOf(component, ViewMatchers.isDescendantOfA(ViewMatchers.withTagValue(tagValue))))
    }

    fun componentInTagged(component: Matcher<View>, key: Int, tagValue: Matcher<Any>): ViewInteraction {
        return Espresso.onView(Matchers.allOf(component, ViewMatchers.isDescendantOfA(ViewMatchers.withTagKey(key, tagValue))))
    }

    fun scrollTo(list: Matcher<View>, item: Matcher<View>) {
        Espresso.onView(list).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(item))
    }

    fun scrollTo(item: Matcher<View>) {
        scrollTo(ViewMatchers.withId(R.id.list), item)
    }

    fun scrollToPosition(list: Matcher<View>, position: Int) {
        Espresso.onView(list).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
    }

    fun scrollToPosition(position: Int) {
        scrollToPosition(ViewMatchers.withId(R.id.list), position)
    }

//    fun getTextByTag(@IdRes id: Int, tag: String) = componentInTagged(ViewMatchers.withId(id), R.id.tag_test, Matchers.equalTo(tag))

    fun assertDialogText(title: Matcher<String>, message: Matcher<String>) {
        Espresso.onView(ViewMatchers.withId(R.id.md_title)).check(ViewAssertions.matches(ViewMatchers.withText(title)))
        Espresso.onView(ViewMatchers.withId(R.id.md_content)).check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    fun assertDialogText(title: Int, message: Matcher<String>) {
        Espresso.onView(ViewMatchers.withId(R.id.md_title)).check(ViewAssertions.matches(ViewMatchers.withText(title)))
        Espresso.onView(ViewMatchers.withId(R.id.md_content)).check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    fun assertDialogText(title: Int, message: Int) {
        Espresso.onView(ViewMatchers.withId(R.id.md_title)).check(ViewAssertions.matches(ViewMatchers.withText(title)))
        Espresso.onView(ViewMatchers.withId(R.id.md_content)).check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    fun assertNeutralText(text: Matcher<String>) {
        Espresso.onView(ViewMatchers.withId(R.id.md_buttonDefaultNeutral)).check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    fun assertNeutralNotVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.md_buttonDefaultNeutral)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    fun clickDialogPositive() {
        clickButton(R.id.md_buttonDefaultPositive)
    }

    fun clickDialogNeutral() {
        clickButton(R.id.md_buttonDefaultNeutral)
    }

    fun clickDialogNegative() {
        clickButton(R.id.md_buttonDefaultNegative)
    }

    fun clickPromptCheckbox() {
        clickButton(R.id.md_promptCheckbox)
    }

    fun clickButton(@IdRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
    }

    fun sleep(ms: Long) {
        SystemClock.sleep(ms)
    }

    fun pressBack() {
        Espresso.closeSoftKeyboard()
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.pressBack()
    }
}