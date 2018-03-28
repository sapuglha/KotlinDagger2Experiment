package com.robotsandpencils.kotlinexperiment.domain.usecases

import com.robotsandpencils.kotlinexperiment.domain.common.Transformer
import com.robotsandpencils.kotlinexperiment.domain.common.UseCase
import com.robotsandpencils.kotlinexperiment.domain.entities.ComicEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
import io.reactivex.Observable

class LoadsComic(transformer: Transformer<ComicEntity>,
                 private val comicRepository: ComicRepository) : UseCase<ComicEntity>(transformer) {

    fun getComic(): Observable<ComicEntity> {
        return observable(null)
    }

    fun getComic(number: Int): Observable<ComicEntity> {
        return observable(mapOf(Pair(PARAM_COMIC_NUMBER, number)))
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ComicEntity> {
        return if (data == null) {
            comicRepository.getComic()
        } else {
            comicRepository.getComic(data[PARAM_COMIC_NUMBER] as Int)
        }
    }

    companion object {
        private const val PARAM_COMIC_NUMBER = "param:comic_number"
    }
}
