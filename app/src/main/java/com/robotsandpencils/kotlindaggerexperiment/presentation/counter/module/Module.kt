package com.robotsandpencils.kotlindaggerexperiment.presentation.counter.module

import com.robotsandpencils.kotlindaggerexperiment.presentation.counter.CounterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

// Main Module: Uses ContributesAndroidInjector to generate a component and builder automatically.
// Using this to provide a presenter module for this scope.
@Module
internal abstract class Module {
    @Scope
    @ContributesAndroidInjector(modules = [(PresenterModule::class)])
    abstract fun provideInjector(): CounterFragment
}