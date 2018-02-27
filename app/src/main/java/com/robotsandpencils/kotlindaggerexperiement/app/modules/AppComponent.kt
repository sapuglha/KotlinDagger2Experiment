package com.robotsandpencils.kotlindaggerexperiement.app.modules

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

/**
 * Main App Component
 */
@AppScope
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class
])
interface AppComponent {
    fun userComponent(): UserComponent.Builder

    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun build(): AppComponent
    }
}