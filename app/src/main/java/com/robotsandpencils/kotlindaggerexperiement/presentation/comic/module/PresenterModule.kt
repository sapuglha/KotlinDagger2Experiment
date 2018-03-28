package com.robotsandpencils.kotlindaggerexperiement.presentation.comic.module

import com.robotsandpencils.kotlindaggerexperiement.presentation.base.LifecycleAwareUiThreadQueue
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.ComicFragment
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.Contract
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.Presenter
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
