package com.robotsandpencils.kotlindaggerexperiement.app.modules

import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.internal.AuthStrategyFactory
import dagger.Subcomponent
import okhttp3.OkHttpClient

@UserScope
@Subcomponent(modules = [
    NetModule::class,
    RepositoryModule::class,
    com.robotsandpencils.kotlindaggerexperiement.presentation.comic.module.Module::class,
    com.robotsandpencils.kotlindaggerexperiement.presentation.counter.module.Module::class,
    com.robotsandpencils.kotlindaggerexperiement.presentation.main.module.Module::class
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
