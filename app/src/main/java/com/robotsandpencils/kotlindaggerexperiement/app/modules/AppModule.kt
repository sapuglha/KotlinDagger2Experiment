package com.robotsandpencils.kotlindaggerexperiement.app.modules

import android.arch.persistence.room.Room
import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlinexperiment.data.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiement.app.managers.PreferencesManager
import dagger.Module
import dagger.Provides

/**
 * App Module
 */

@Module
open class AppModule(val app: App) {
    @Provides
    @AppScope
    fun provideApp() = app

    @Provides
    @AppScope
    open fun providePreferencesManager() = PreferencesManager(app)

    @Provides
    @AppScope
    fun provideAppDatabase(app: App) : AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "database-name").build()
    }

}