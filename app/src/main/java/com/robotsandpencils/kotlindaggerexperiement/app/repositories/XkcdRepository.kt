package com.robotsandpencils.kotlindaggerexperiement.app.repositories

import arrow.core.Either
import com.jakewharton.rxrelay2.BehaviorRelay
import com.robotsandpencils.kotlindaggerexperiement.net.xkcd.XkcdAPI
import com.robotsandpencils.kotlindaggerexperiement.net.xkcd.XkcdResponse

class XkcdRepository(private val api: XkcdAPI) : BaseRepository() {

    val responseRelay: BehaviorRelay<Either<Throwable, XkcdResponse>> = BehaviorRelay.create()

    fun syncLatestComic() {
        syncModel(api.getLatestComic(), responseRelay)
    }

    fun syncComic(num: Int) {
        syncModel(api.getComic(num), responseRelay)
    }
}
