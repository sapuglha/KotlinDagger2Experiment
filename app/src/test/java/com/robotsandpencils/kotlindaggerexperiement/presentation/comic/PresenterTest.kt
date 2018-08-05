package com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jakewharton.rxrelay2.BehaviorRelay
import com.nhaarman.mockitokotlin2.*
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import com.robotsandpencils.kotlinexperiment.domain.entities.ComicEntity
import com.robotsandpencils.kotlinexperiment.domain.repositories.ComicRepository
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.internal.verification.Times


class PresenterTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var presenter: Presenter
    val repository = mock<ComicRepository>()
    val uiThreadQueue = mock<UiThreadQueue>()
    val view = mock<Contract.View>()
    val comicObservable: Observable<ComicEntity> = BehaviorRelay.create()

    @Before
    fun before() {
        presenter = Presenter(repository, uiThreadQueue)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        whenever(repository.getComic()).thenReturn(comicObservable)
        whenever(repository.getComic(any())).thenReturn(comicObservable)
        /*
        whenever(domain.requestLatestComic()).thenReturn(
                Single.just(Either.Right(
                        XkcdResponse(month = "9", alt = "Alt", day = "9", img = "Image",
                                link = "link", news = "News", num = 1, safeTitle = "Safe title",
                                title = "Title", transcript = "Transcript", year = "1999").toDomain())))

        whenever(domain.requestComic(any())).thenReturn(
                Single.just(
                        XkcdResponse(month = "9", alt = "Alt", day = "9", img = "Image",
                                link = "link", news = "News", num = 10, safeTitle = "Safe title",
                                title = "Title", transcript = "Transcript", year = "1999")))
                                */


    }

    @Test
    fun testAttach_shouldHandleNullViewModel() {
        whenever(view.getViewModel()).thenReturn(null)
        presenter.attach(view)

        verifyZeroInteractions(repository)
    }

    @Test
    fun testAttach_shouldAskForLatest() {
        val vm = ComicViewModel()
        whenever(view.getViewModel()).thenReturn(vm)

        presenter.attach(view)

        verify(repository).getComic()
    }

    @Test
    fun testAttach_shouldAskForSame() {
        val vm = ComicViewModel()
        vm.state.value = ComicState.ComicLoaded("Title", "url", 10)
        whenever(view.getViewModel()).thenReturn(vm)

        presenter.attach(view)

        val captor: KArgumentCaptor<Int> = argumentCaptor()
        verify(repository, Times(1)).getComic(captor.capture())

        Assert.assertThat(captor.firstValue, `is`(10))
    }

    @Test
    fun testOnPrevious() {
        whenever(view.getViewModel()).thenReturn(null)

        presenter.attach(view)

        val vm = ComicViewModel()
        vm.state.value = ComicState.ComicLoaded("Title", "url", 10)
        whenever(view.getViewModel()).thenReturn(vm)

        presenter.showPreviousComic()

        val captor: KArgumentCaptor<Int> = argumentCaptor()
        verify(repository, Times(1)).getComic(captor.capture())

        Assert.assertThat(captor.firstValue, `is`(9))
    }
}