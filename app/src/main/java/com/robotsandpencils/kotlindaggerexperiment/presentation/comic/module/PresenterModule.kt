package com.robotsandpencils.kotlindaggerexperiment.presentation.comic.module

import com.robotsandpencils.kotlindaggerexperiment.presentation.base.LifecycleAwareUiThreadQueue
import com.robotsandpencils.kotlindaggerexperiment.presentation.comic.ComicFragment
import com.robotsandpencils.kotlindaggerexperiment.presentation.comic.Contract
import com.robotsandpencils.kotlindaggerexperiment.presentation.comic.Presenter
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
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
        internal fun providesPresenter(fragment: ComicFragment, repository: ComicRepository): Contract.Presenter {
            return Presenter(repository, LifecycleAwareUiThreadQueue(fragment))
        }
    }
}
