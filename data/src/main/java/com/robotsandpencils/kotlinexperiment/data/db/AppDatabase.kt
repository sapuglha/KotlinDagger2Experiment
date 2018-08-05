package com.robotsandpencils.kotlinexperiment.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.robotsandpencils.kotlinexperiment.data.db.User
import com.robotsandpencils.kotlinexperiment.data.db.UserDao

/**
 * Created by nealsanche on 2017-09-08.
 */

@Database(entities = [(User::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}