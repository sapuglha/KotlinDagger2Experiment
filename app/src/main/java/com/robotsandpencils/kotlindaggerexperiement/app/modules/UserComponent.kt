package com.robotsandpencils.kotlindaggerexperiement.app.modules

import com.robotsandpencils.kotlindaggerexperiement.App
import dagger.Subcomponent

@UserScope
@Subcomponent(modules = [
    com.robotsandpencils.kotlindaggerexperiement.presentation.comic.module.Module::class,
    com.robotsandpencils.kotlindaggerexperiement.presentation.counter.module.Module::class,
    com.robotsandpencils.kotlindaggerexperiement.presentation.main.module.Module::class
])
interface UserComponent {

    fun inject(app: App)

    @Subcomponent.Builder
    interface Builder {
        fun build(): UserComponent
    }
}
