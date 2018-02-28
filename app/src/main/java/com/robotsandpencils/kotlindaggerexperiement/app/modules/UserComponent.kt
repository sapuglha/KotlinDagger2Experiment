package com.robotsandpencils.kotlindaggerexperiement.app.modules

import com.robotsandpencils.kotlindaggerexperiement.App
import dagger.Subcomponent

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

    @Subcomponent.Builder
    interface Builder {
        fun netModule(module: NetModule): Builder
        fun repositoryModule(module: RepositoryModule): Builder
        fun build(): UserComponent
    }
}
