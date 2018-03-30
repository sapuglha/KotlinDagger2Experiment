package com.robotsandpencils.kotlinexperiment.domain.repositories

import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import io.reactivex.Observable

interface UserRepository {
    fun getCount(): Observable<Int>
    fun getAll(): Observable<List<UserEntity>>
    fun insertAll(vararg users: UserEntity)
    fun delete(user: UserEntity)
}