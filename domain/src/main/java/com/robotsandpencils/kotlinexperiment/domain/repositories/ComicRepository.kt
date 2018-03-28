package com.robotsandpencils.kotlinexperiment.domain.repositories

import com.robotsandpencils.kotlinexperiment.domain.entities.ComicEntity
import io.reactivex.Observable

interface ComicRepository {
    fun getComic(): Observable<ComicEntity>
    fun getComic(number: Int): Observable<ComicEntity>
}