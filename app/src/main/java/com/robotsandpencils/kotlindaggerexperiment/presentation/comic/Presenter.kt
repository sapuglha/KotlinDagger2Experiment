package com.robotsandpencils.kotlindaggerexperiment.presentation.comic

import arrow.core.Either
import com.jakewharton.rxrelay2.PublishRelay
import com.robotsandpencils.kotlindaggerexperiment.app.common.AsyncTransformer
import com.robotsandpencils.kotlindaggerexperiment.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiment.presentation.base.UiThreadQueue
import com.robotsandpencils.kotlinexperiment.domain.entities.ComicEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
import com.robotsandpencils.kotlinexperiment.domain.usecases.LoadsComic
import retrofit2.HttpException
import timber.log.Timber

class Presenter(private val repository: ComicRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    private val useCase = LoadsComic(AsyncTransformer(), repository)

    private val comic: PublishRelay<Either<Throwable, ComicEntity>> = PublishRelay.create()

    override fun attach(view: Contract.View) {
        super.attach(view)

        view.getViewModel()?.let { viewModel ->
            val state = viewModel.state.value
            when (state) {
                is ComicState.ComicLoaded -> {
                    val currentNum = state.comicNumber
                    requestComic(currentNum)
                }
                else -> {
                    updateViewModel(ComicState.Loading())
                    requestLatestComic()
                }
            }
        }

        comic.subscribe {
            when (it) {
                is Either.Right -> {
                    val comic = it.b
                    updateViewModel(ComicState.ComicLoaded(
                            comic.title,
                            comic.url,
                            comic.number))

                }
                is Either.Left -> {

                    val ex = it.a

                    when (ex) {
                        is HttpException -> {
                            if (ex.code() == 404) {
                                // We expect a 404 if you go past the end so, just ignore it
                                return@subscribe
                            }
                        }
                    }

                    Timber.e(ex, "Unhandled Error at Presenter")
                    updateViewModel(ComicState.Error())
                }
            }
        }.also {
            disposables.add(it)
        }
    }

    private fun updateViewModel(newState: ComicState) {
        view?.getViewModel()?.apply {
            state.postValue(newState)
        }
    }

    override fun showPreviousComic() {
        getComic(-1)
    }

    override fun showNextComic() {
        getComic(1)
    }

    private fun getComic(delta: Int) {
        view?.getViewModel()?.let { vm ->
            val state = vm.state.value
            when (state) {
                is ComicState.ComicLoaded -> {
                    val currentNum = state.comicNumber
                    requestComic(currentNum + delta)
                }
                else -> requestLatestComic()
            }
        }
    }

    private fun requestLatestComic() {

        useCase.getComic().subscribe(
                {
                    comic.accept(Either.Right(it))
                },
                { error ->
                    comic.accept(Either.Left(error))
                }
        ).also {
            disposables.add(it)
        }
    }

    private fun requestComic(comicNumber: Int) {

        useCase.getComic(comicNumber).subscribe(
                { comic.accept(Either.Right(it)) },
                {
                    comic.accept(Either.Left(it))
                })
                .also {
                    disposables.add(it)
                }
    }
}