package com.robotsandpencils.kotlindaggerexperiment

import androidx.test.InstrumentationRegistry

class TestHelper {
    companion object {
        fun getApp(): App = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
    }
}
