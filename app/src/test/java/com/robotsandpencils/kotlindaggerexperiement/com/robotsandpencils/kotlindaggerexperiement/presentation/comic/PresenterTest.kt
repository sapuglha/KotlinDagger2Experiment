package com.robotsandpencils.kotlindaggerexperiement.com.robotsandpencils.kotlindaggerexperiement.presentation.comic

import android.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.Either
import com.jakewharton.rxrelay2.BehaviorRelay
import com.nhaarman.mockitokotlin2.*
import com.robotsandpencils.kotlindaggerexperiement.domain.comic.Comic
import com.robotsandpencils.kotlindaggerexperiement.domain.comic.ComicDomainModel
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.ComicState
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.ComicViewModel
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.Contract
import com.robotsandpencils.kotlindaggerexperiement.presentation.comic.Presenter
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
    val domain = mock<ComicDomainModel>()
    val uiThreadQueue = mock<UiThreadQueue>()
    val view = mock<Contract.View>()
    val comicObservable: Observable<Either<Throwable, Comic>> = BehaviorRelay.create()

    @Before
    fun before() {
        presenter = Presenter(domain, uiThreadQueue)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        whenever(domain.comic).thenReturn(comicObservable)
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

        verify(domain).attach()
    }

    @Test
    fun testAttach_shouldAskForLatest() {
        val vm = ComicViewModel()
        whenever(view.getViewModel()).thenReturn(vm)

        presenter.attach(view)

        verify(domain, Times(1)).requestLatestComic()
    }

    @Test
    fun testAttach_shouldAskForSame() {
        val vm = ComicViewModel()
        vm.state.value = ComicState.ComicLoaded("Title", "url", 10)
        whenever(view.getViewModel()).thenReturn(vm)

        presenter.attach(view)

        val captor: KArgumentCaptor<Int> = argumentCaptor()
        verify(domain, Times(1)).requestComic(captor.capture())

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
        verify(domain, Times(1)).requestComic(captor.capture())

        Assert.assertThat(captor.firstValue, `is`(9))
    }
}