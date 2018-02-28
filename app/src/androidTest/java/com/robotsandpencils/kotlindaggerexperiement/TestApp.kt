package com.robotsandpencils.kotlindaggerexperiement

import com.robotsandpencils.kotlindaggerexperiement.app.modules.AppComponent
import com.robotsandpencils.kotlindaggerexperiement.app.modules.AppModule
import com.robotsandpencils.kotlindaggerexperiement.app.modules.DaggerAppComponent
import com.robotsandpencils.kotlindaggerexperiement.app.modules.UserComponent
import timber.log.Timber

/**
 * Provides a way to replace some of the services globally, but try using DaggerMock instead if you
 * want to mock various providers.
 */
class TestApp : App() {

    override fun createComponent(): AppComponent {

        Timber.e("**** CREATED COMPONENT")

        return DaggerAppComponent
                .builder()
                .appModule(TestAppModule(this))
                .build()
    }

    override fun createUserComponent(): UserComponent {
        Timber.e("**** CREATED USER COMPONENT")
        return component.userComponent()
                .build()

    }

    fun clearComponent() {
        this.resetUserComponent()
    }

    open class TestAppModule(app: App) : AppModule(app)
}
