package com.robotsandpencils.kotlinexperiment.data.repositories

import com.robotsandpencils.kotlinexperiment.data.api.xkcd.ComicApi
import com.robotsandpencils.kotlinexperiment.data.api.xkcd.toDomain
import com.robotsandpencils.kotlinexperiment.domain.entities.ComicEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
import io.reactivex.Observable

class ComicRepositoryImpl(val api: ComicApi) : ComicRepository {
    override fun getComic(): Observable<ComicEntity> {
        return api.getLatestComic().map {
            it.toDomain()
        }
    }

    override fun getComic(number: Int): Observable<ComicEntity> {
        return api.getComic(number).map {
            it.toDomain()
        }
    }
}
