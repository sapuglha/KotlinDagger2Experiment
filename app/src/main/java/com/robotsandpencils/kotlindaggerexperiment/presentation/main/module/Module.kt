package com.robotsandpencils.kotlindaggerexperiment.presentation.main.module

import com.robotsandpencils.kotlindaggerexperiment.presentation.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

// Main Module: Uses ContributesAndroidInjector to generate a component and builder automatically.
// Using this to provide a presenter module for this scope.
@Module
internal abstract class Module {
    @Scope
    @ContributesAndroidInjector(modules = [(PresenterModule::class)])
    abstract fun provideMainActivityInjector(): MainActivity
}