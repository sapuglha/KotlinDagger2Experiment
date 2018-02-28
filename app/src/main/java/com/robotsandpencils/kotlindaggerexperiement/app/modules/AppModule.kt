package com.robotsandpencils.kotlindaggerexperiement.app.modules

import android.arch.persistence.room.Room
import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.db.AppDatabase
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
    fun provideAppDatabase(app: App) : AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "database-name").build()
    }

}