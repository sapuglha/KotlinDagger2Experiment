package com.robotsandpencils.kotlinexperiment.domain.usecases

import com.robotsandpencils.kotlinexperiment.domain.common.Transformer
import com.robotsandpencils.kotlinexperiment.domain.common.UseCase
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import io.reactivex.Observable

class ManageUsers(transformer: Transformer<List<UserEntity>>,
                  private val repository: UserRepository) : UseCase<List<UserEntity>>(transformer) {

    fun getUsers(): Observable<List<UserEntity>> {
        return observable(null)
    }

    fun addUsers(vararg users: UserEntity) {
        repository.insertAll(*users)
    }

    fun deleteUser(user: UserEntity) {
        repository.delete(user)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<UserEntity>> =
            repository.getAll()

}
