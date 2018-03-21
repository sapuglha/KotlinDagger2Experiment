package com.robotsandpencils.kotlindaggerexperiement.domain.comic

import arrow.core.Either
import com.jakewharton.rxrelay2.BehaviorRelay
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.XkcdRepository
import com.robotsandpencils.kotlindaggerexperiement.domain.base.DomainModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class ComicDomainModel(private val repository: XkcdRepository) : DomainModel {
    val comic: Observable<Either<Throwable, Comic>> = BehaviorRelay.create()

    private val disposables = CompositeDisposable()

    private fun updateComic(it: Either<Throwable, Comic>) {
        (comic as BehaviorRelay).accept(it)
    }

    fun requestLatestComic() {
        repository.syncLatestComic()
    }

    fun requestComic(num: Int) {
        repository.syncComic(num)
    }

    override fun attach() {
        repository.responseRelay
                .map {
                    when (it) {
                        is Either.Right -> updateComic(Either.Right(it.b.toDomain()))
                        is Either.Left -> updateComic(Either.Left(it.a))
                    }
                }
                .subscribe()
                .also {
                    disposables.add(it)
                }
    }

    override fun detach() {
        disposables.dispose()
    }
}