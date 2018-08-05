package com.robotsandpencils.kotlinexperiment.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by nealsanche on 2017-09-08.
 */

@Database(entities = [(User::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}