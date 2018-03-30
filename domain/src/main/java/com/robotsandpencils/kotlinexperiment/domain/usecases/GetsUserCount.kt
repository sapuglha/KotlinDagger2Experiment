package com.robotsandpencils.kotlinexperiment.domain.usecases

import com.robotsandpencils.kotlinexperiment.domain.common.Transformer
import com.robotsandpencils.kotlinexperiment.domain.common.UseCase
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import io.reactivex.Observable

class GetsUserCount(transformer: Transformer<Int>,
                    private val repository: UserRepository) : UseCase<Int>(transformer) {

    fun getUserCount(): Observable<Int> {
        return observable(null)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Int> =
            repository.getCount()
}
