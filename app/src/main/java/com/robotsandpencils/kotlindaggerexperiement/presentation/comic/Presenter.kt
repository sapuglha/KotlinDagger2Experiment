package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import com.robotsandpencils.kotlindaggerexperiement.app.repositories.XkcdRepository
import com.robotsandpencils.kotlindaggerexperiement.net.xkcd.XkcdResponse
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter(private val repository: XkcdRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    override fun attach(view: Contract.View) {
        super.attach(view)

        view.getViewModel()?.let { viewModel ->
            val state = viewModel.state.value
            when (state) {
                is ComicState.ComicLoaded -> {
                    val currentNum = state.comicNumber
                    requestComic(currentNum, viewModel)
                }
                else -> {
                    viewModel.state.value = ComicState.Loading()
                    requestLatestComic(viewModel)
                }
            }
        }
    }

    override fun showPreviousComic() {
        view?.getViewModel()?.let { vm ->
            val state = vm.state.value
            when (state) {
                is ComicState.ComicLoaded -> {
                    val currentNum = state.comicNumber
                    requestComic(currentNum - 1, vm)
                }
                else -> requestLatestComic(vm)
            }
        }
    }

    private fun requestLatestComic(viewModel: ComicViewModel) {
        requestAndUpdateViewModel(repository.getLatestComic(), viewModel)
    }

    private fun requestComic(comicNumber: Int, viewModel: ComicViewModel) {
        requestAndUpdateViewModel(repository.getComic(comicNumber), viewModel)
    }

    private fun requestAndUpdateViewModel(request: Single<XkcdResponse>, viewModel: ComicViewModel) {
        view?.getViewModel()?.state?.postValue(ComicState.Loading())

        disposables.add(request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { updateViewModel(viewModel, it) },
                        { view?.getViewModel()?.state?.postValue(ComicState.Error()) }
                ))
    }

    private fun updateViewModel(viewModel: ComicViewModel, response: XkcdResponse) {
        viewModel.apply {
            state.postValue(ComicState.ComicLoaded(response.title, response.img, response.num))
        }
    }
}