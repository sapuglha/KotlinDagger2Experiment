package com.robotsandpencils.kotlindaggerexperiment.app.modules

import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlinexperiment.data.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
import com.robotsandpencils.kotlindaggerexperiment.app.repositories.AuthRepository
import com.robotsandpencils.kotlinexperiment.data.api.xkcd.ComicApi
import com.robotsandpencils.kotlinexperiment.data.repositories.ComicRepositoryImpl
import com.robotsandpencils.kotlinexperiment.data.repositories.UserRepositoryImpl
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import dagger.Module
import dagger.Provides

@Module
open class RepositoryModule {
    @Provides
    @UserScope
    fun provideAuthRepository(app: App, preferencesManager: PreferencesManager) = AuthRepository(app, preferencesManager)

    @Provides
    @UserScope
    fun provideUserRepository(database: AppDatabase): UserRepository = UserRepositoryImpl(database)

    @Provides
    @UserScope
    fun provideComicRepository(api: ComicApi): ComicRepository = ComicRepositoryImpl(api)
}
