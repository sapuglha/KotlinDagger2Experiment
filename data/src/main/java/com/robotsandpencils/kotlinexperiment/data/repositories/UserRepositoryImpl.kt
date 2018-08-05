package com.robotsandpencils.kotlinexperiment.data.repositories

import com.robotsandpencils.kotlinexperiment.data.db.AppDatabase
import com.robotsandpencils.kotlinexperiment.data.db.toData
import com.robotsandpencils.kotlinexperiment.data.db.toDomain
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import io.reactivex.Observable

class UserRepositoryImpl(val db: AppDatabase) : UserRepository {

    override fun getCount(): Observable<Int> {
        return db.userDao().getCount().toObservable()
    }

    override fun getAll(): Observable<List<UserEntity>> {
        return db.userDao().getAll().map {
            it.map {
                it.toDomain()
            }
        }.toObservable()
    }

    override fun insertAll(vararg users: UserEntity) {
        db.userDao().insertAll(*users.map({ it.toData() }).toTypedArray())
    }

    override fun delete(user: UserEntity) {
        db.userDao().delete(user.toData())
    }
}
