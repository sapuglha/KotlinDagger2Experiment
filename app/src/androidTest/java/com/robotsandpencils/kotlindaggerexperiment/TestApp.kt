package com.robotsandpencils.kotlindaggerexperiment

import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
import com.robotsandpencils.kotlindaggerexperiment.app.model.Environment
import com.robotsandpencils.kotlindaggerexperiment.app.model.Environments
import com.robotsandpencils.kotlindaggerexperiment.app.modules.*
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
                .netModule(TestNetModule())
                .repositoryModule(RepositoryModule())
                .build()

    }

    fun clearComponent() {
        this.resetUserComponent()
    }

    open class TestNetModule : NetModule() {
        override fun provideEnvironment(environments: Environments, preferencesManager: PreferencesManager): Environment =
                Environment.createTestEnvironment("http://localhost:8080")
    }

    open class TestAppModule(app: App) : AppModule(app)
}
