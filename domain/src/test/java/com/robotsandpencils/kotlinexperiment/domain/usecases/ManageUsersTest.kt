package com.robotsandpencils.kotlinexperiment.domain.usecases

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.robotsandpencils.kotlinexperiment.common.TestTransformer
import com.robotsandpencils.kotlinexperiment.domain.entities.UserEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import io.reactivex.Observable
import org.junit.Test

class ManageUsersTest {

    @Test
    fun getUsers() {
        val entity = UserEntity(0, "Tony", "Stark")
        val repository: UserRepository = mock()
        val useCase = ManageUsers(TestTransformer(), repository)

        whenever(repository.getAll()).thenReturn(Observable.just(listOf(entity)))

        useCase.getUsers().test()
                .assertComplete()
                .assertValueCount(1)
    }

    @Test
    fun delete() {
        val entity = UserEntity(0, "Tony", "Stark")
        val repository: UserRepository = mock()
        val useCase = ManageUsers(TestTransformer(), repository)

        useCase.deleteUser(entity)

        verify(repository).delete(entity)
    }

    @Test
    fun add() {
        val entity = UserEntity(0, "Tony", "Stark")
        val entity2 = UserEntity(2, "Pepper", "Potts")
        val repository: UserRepository = mock()
        val useCase = ManageUsers(TestTransformer(), repository)

        useCase.addUsers(entity, entity2)

        verify(repository).insertAll(entity, entity2)
    }
}