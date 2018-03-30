package com.robotsandpencils.kotlindaggerexperiement.presentation.counter

import com.robotsandpencils.kotlindaggerexperiement.app.common.AsyncTransformer
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import com.robotsandpencils.kotlinexperiment.domain.repositories.UserRepository
import com.robotsandpencils.kotlinexperiment.domain.usecases.GetsUserCount

class Presenter(repository: UserRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    private val getsUserCount: GetsUserCount = GetsUserCount(AsyncTransformer(), repository)

    override fun attach(view: Contract.View) {
        super.attach(view)

        getsUserCount.getUserCount().subscribe {
            view.getViewModel()?.apply {
                count.postValue(it)
            }
        }.also {
            disposables.add(it)
        }
    }
}