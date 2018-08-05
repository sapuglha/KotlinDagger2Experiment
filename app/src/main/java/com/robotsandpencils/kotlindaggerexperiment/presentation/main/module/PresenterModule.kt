package com.robotsandpencils.kotlindaggerexperiment.presentation.main.module

import com.robotsandpencils.kotlindaggerexperiment.presentation.base.LifecycleAwareUiThreadQueue
import com.robotsandpencils.kotlindaggerexperiment.presentation.main.Contract
import com.robotsandpencils.kotlindaggerexperiment.presentation.main.MainActivity
import com.robotsandpencils.kotlindaggerexperiment.presentation.main.Presenter
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import dagger.Module
import dagger.Provides

// This Kotlin file construction seems odd, but follows from this:
// https://stackoverflow.com/questions/44075860/module-must-be-set
@Module
internal abstract class PresenterModule {
    @Module
    companion object {
        @Provides
        @Scope
        @JvmStatic
        internal fun providesPresenter(activity: MainActivity, repository: UserRepository): Contract.Presenter {
            return Presenter(repository, LifecycleAwareUiThreadQueue(activity))
        }
    }
}
