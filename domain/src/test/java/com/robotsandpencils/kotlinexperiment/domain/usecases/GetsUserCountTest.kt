package com.robotsandpencils.kotlinexperiment.domain.usecases

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.robotsandpencils.kotlinexperiment.common.TestTransformer
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import io.reactivex.Observable
import org.junit.Test

class GetsUserCountTest {

    @Test
    fun getsUserCount() {
        val count = 0
        val repository: UserRepository = mock()
        val useCase = GetsUserCount(TestTransformer(), repository)

        whenever(repository.getCount()).thenReturn(Observable.just(count))

        useCase.getUserCount().test()
                .assertComplete()
                .assertValueCount(1)
    }
}