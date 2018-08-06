package com.robotsandpencils.kotlindaggerexperiment.base

import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlindaggerexperiment.TestApp
import com.robotsandpencils.kotlindaggerexperiment.app.modules.*
import com.robotsandpencils.kotlindaggerexperiment.tests.MockTest
import dagger.Component
import dagger.Subcomponent
import dagger.android.support.AndroidSupportInjectionModule

class BaseDaggerTestHarness(val app: App,
                            netModule: NetModule = TestApp.TestNetModule(),
                            repositoryModule: RepositoryModule = RepositoryModule(),
                            appModule: AppModule = TestApp.TestAppModule(app)) {

    private val component: TestAppComponent
    val userComponent: TestUserComponent

    init {
        // Build test app component
        component = DaggerBaseDaggerTestHarness_TestAppComponent.builder()
                .appModule(appModule)
                .build()

        // Build a userComponent that replaces the authRepository
        userComponent = component.testUserComponent()
                .netModule(netModule)
                .repositoryModule(repositoryModule).build()

        app.component = component
        app.userComponent = userComponent
    }

    @AppScope
    @Component(modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class]
    )
    interface TestAppComponent : AppComponent {

        fun testUserComponent(): TestUserComponent.Builder

        @Component.Builder
        interface Builder {
            fun appModule(module: AppModule): Builder
            fun build(): TestAppComponent
        }
    }

    // This needs to be overridden so the test can be injected
    @UserScope
    @Subcomponent(modules = [
        RepositoryModule::class,
        NetModule::class])
    interface TestUserComponent : UserComponent {

        @Subcomponent.Builder
        interface Builder {
            fun netModule(module: NetModule): Builder
            fun repositoryModule(module: RepositoryModule): Builder
            fun build(): TestUserComponent
        }

        // Need to add inject methods here for any
        // classes that need to be injected

        fun inject(mockTest: MockTest)
    }
}