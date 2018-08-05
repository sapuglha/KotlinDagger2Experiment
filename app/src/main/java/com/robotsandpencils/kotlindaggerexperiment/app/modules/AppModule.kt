package com.robotsandpencils.kotlindaggerexperiment.app.modules

import androidx.room.Room
import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlinexperiment.data.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
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