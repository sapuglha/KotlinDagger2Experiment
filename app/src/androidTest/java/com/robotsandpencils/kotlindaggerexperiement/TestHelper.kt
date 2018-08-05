package com.robotsandpencils.kotlindaggerexperiement

import androidx.test.InstrumentationRegistry

class TestHelper {
    companion object {
        fun getApp(): App = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
    }
}
