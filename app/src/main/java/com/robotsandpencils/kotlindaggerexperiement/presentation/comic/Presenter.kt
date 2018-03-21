package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import arrow.core.Either
import com.robotsandpencils.kotlindaggerexperiement.domain.comic.ComicDomainModel
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import retrofit2.HttpException
import timber.log.Timber

class Presenter(private val model: ComicDomainModel, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    override fun attach(view: Contract.View) {
        super.attach(view)

        model.attach()

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

        model.comic.subscribe {
            when (it) {
                is Either.Right -> {
                    val comic = it.b
                    updateViewModel(ComicState.ComicLoaded(
                            comic.title,
                            comic.img,
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

    override fun detach() {
        super.detach()
        model.detach()
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
        model.requestLatestComic()
    }

    private fun requestComic(comicNumber: Int) {
        model.requestComic(comicNumber)
    }
}