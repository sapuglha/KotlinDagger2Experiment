package com.robotsandpencils.kotlinexperiment.domain.usecases

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.robotsandpencils.kotlinexperiment.common.TestTransformer
import com.robotsandpencils.kotlinexperiment.domain.entities.ComicEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test

class LoadsComicTest {

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {
    }

    @Test
    fun getComic() {
        val entity = ComicEntity("Comic", 10, "url")
        val repository: ComicRepository = mock()
        val useCase = LoadsComic(TestTransformer(), repository)

        whenever(repository.getComic()).thenReturn(Observable.just(entity))

        useCase.getComic().test()
                .assertComplete()
                .assertValueCount(1)
    }

    @Test
    fun getComic1() {
    }
}