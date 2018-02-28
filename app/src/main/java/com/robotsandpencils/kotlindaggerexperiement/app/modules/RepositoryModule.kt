package com.robotsandpencils.kotlindaggerexperiement.app.modules

import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiement.app.managers.PreferencesManager
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.AuthRepository
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.XkcdRepository
import com.robotsandpencils.kotlindaggerexperiement.net.xkcd.XkcdAPI
import dagger.Module
import dagger.Provides

@Module
open class RepositoryModule {
    @Provides
    @UserScope
    fun provideAuthRepository(app: App, preferencesManager: PreferencesManager) = AuthRepository(app, preferencesManager)

    @Provides
    @UserScope
    fun provideMainRepository(app: App, database: AppDatabase) = MainRepository(app, database)

    @Provides
    @UserScope
    fun provideXkcdRepository(api: XkcdAPI) = XkcdRepository(api)
}
