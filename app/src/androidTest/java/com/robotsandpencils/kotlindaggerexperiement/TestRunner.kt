package com.robotsandpencils.kotlindaggerexperiement

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import com.github.tmurakami.dexopener.DexOpener

//import com.github.tmurakami.dexopener.DexOpener

class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {

        // Kotlin classes are final by default, this library fixes this so that mocking can be done
        // to classes
        DexOpener.install(this)

        return super.newApplication(cl, "com.robotsandpencils.kotlindaggerexperiement.TestApp", context)
    }
}