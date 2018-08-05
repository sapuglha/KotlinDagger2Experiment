package com.robotsandpencils.kotlindaggerexperiment.app.modules

import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlindaggerexperiment.app.internal.AuthStrategyFactory
import dagger.Subcomponent
import okhttp3.OkHttpClient

@UserScope
@Subcomponent(modules = [
    NetModule::class,
    RepositoryModule::class,
    com.robotsandpencils.kotlindaggerexperiment.presentation.comic.module.Module::class,
    com.robotsandpencils.kotlindaggerexperiment.presentation.counter.module.Module::class,
    com.robotsandpencils.kotlindaggerexperiment.presentation.main.module.Module::class
])
interface UserComponent {

    fun inject(app: App)
    fun inject(app: AuthStrategyFactory)

    fun client(): OkHttpClient

    @Subcomponent.Builder
    interface Builder {
        fun netModule(module: NetModule): Builder
        fun repositoryModule(module: RepositoryModule): Builder
        fun build(): UserComponent
    }
}
