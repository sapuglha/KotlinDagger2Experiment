package com.robotsandpencils.kotlindaggerexperiment.tests

import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlindaggerexperiment.TestHelper
import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
import com.robotsandpencils.kotlindaggerexperiment.app.modules.AppModule
import com.robotsandpencils.kotlindaggerexperiment.base.BaseDaggerTestHarness
import com.robotsandpencils.kotlindaggerexperiment.base.BaseTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

/**
 * Just a test to show how to write a test that mocks or uses
 * injection from a component. If you use the @InjectFromComponent ensure
 * there is a method that returns the type of object you want to use
 * in the AppComponent interface. Dagger will happily generate an accessor
 * for that value, but without it, you'll be forever wondering why your
 * object is null in your test.
 */
class MockTest : BaseTest() {
    @Inject
    lateinit var preferencesManager: PreferencesManager

    // Just need to override the preferences manager to make it a mock
    class TestAppModule(app: App) : AppModule(app) {
        override fun providePreferencesManager(): PreferencesManager = mock()
    }

    @Before
    override fun before() {
        super.before()

        val app = TestHelper.getApp()

        BaseDaggerTestHarness(app = app, appModule = TestAppModule(app))
                .userComponent.inject(this)
    }

    /**
     * This test is a little contrived, but just serves to show that indeed, the preferencesManager
     * is mocked and returning the appropriate value when called with appropriate parameters.
     * This also shows some of the convenient stuff in the mockito_kotlin library such as a nice
     * way to avoid back-ticks when using mockito when (use whenever instead) for example.
     */
    @Test
    fun mockPreferencesManagerTest() {
        whenever(preferencesManager.getString(eq("key"), anyOrNull()))
                .thenReturn("Testing")

        Assert.assertThat(preferencesManager.getString("key", null), Matchers.equalTo("Testing"))
    }
}