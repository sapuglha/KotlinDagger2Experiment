package com.robotsandpencils.kotlindaggerexperiement

import android.support.test.InstrumentationRegistry

class TestHelper {
    companion object {
        fun getApp(): App = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
    }
}
