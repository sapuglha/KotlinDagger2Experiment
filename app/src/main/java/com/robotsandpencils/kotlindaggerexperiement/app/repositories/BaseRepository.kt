package com.robotsandpencils.kotlindaggerexperiement.app.repositories

import android.annotation.SuppressLint
import arrow.core.Either
import com.google.gson.GsonBuilder
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.robotsandpencils.kotlindaggerexperiement.app.model.ErrorResponse
import com.robotsandpencils.kotlindaggerexperiement.app.model.ErrorResponseError
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

open class BaseRepository {

    data class BusyState(val busy: Boolean)

    val busyRelay: BehaviorRelay<BusyState> = BehaviorRelay.create<BusyState>()
    val errorRelay: PublishRelay<ErrorResponseError> = PublishRelay.create<ErrorResponseError>()
    val exceptionRelay: PublishRelay<Throwable> = PublishRelay.create<Throwable>()

    private val inFlightCount = AtomicInteger(0)

    private fun <T> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
        }
    }

    /** This is for requests that have no response body */
    @SuppressLint("CheckResult")
    internal fun sync(modelObservable: Observable<Response<ResponseBody>>,
                      onSuccess: () -> Unit = {},
                      onError: (Int?, ErrorResponseError?) -> Boolean = { _, _ -> false }) {
        busy()
        modelObservable
                .compose(applySchedulers())
                .subscribe({ resp ->
                    notBusy()
                    if (resp.isSuccessful) {
                        onSuccess()
                    } else {
                        try {
                            val error = GsonBuilder().create().fromJson(
                                    resp.errorBody()?.charStream(),
                                    ErrorResponse::class.java)
                            if (!onError(resp.code(), error.error)) {
                                errorRelay.accept(error.error)
                            }
                        } catch (e: Throwable) {
                            Timber.e(e)
                            exceptionRelay.accept(e)
                        }
                    }
                }) { exception ->
                    handleException(exception, onError)
                }
    }

    /** This is for requests that have a response body, when no success or error handling is required */
    @SuppressLint("CheckResult")
    internal fun <T> syncModel(modelObservable: Observable<T>, relay: Relay<Either<Throwable, T>>, analyticsFilter: (T) -> Unit = { }) {
        busy()
        modelObservable
                .compose(applySchedulers())
                .filter { it ->
                    analyticsFilter(it)
                    true
                }
                .subscribe({ model ->
                    notBusy()
                    relay.accept(Either.Right(model))
                }) { exception ->
                    handleException(exception)
                    relay.accept(Either.Left(exception))
                }
    }

    /** This is for requests that have a response body, and success and error handling is required */
    @SuppressLint("CheckResult")
    internal fun <T> syncModel(modelObservable: Observable<T>,
                               relay: Relay<T>,
                               onSuccess: (T) -> Unit,
                               onError: (Int?, ErrorResponseError?) -> Boolean = { _, _ -> false },
                               analyticsFilter: (T) -> Unit = { }) {
        busy()
        modelObservable
                .compose(applySchedulers())
                .filter { it ->
                    analyticsFilter(it)
                    true
                }
                .subscribe({ model ->
                    notBusy()
                    relay.accept(model)
                    onSuccess(model)
                }) { exception ->
                    handleException(exception, onError)
                }
    }

    private fun handleException(exception: Throwable,
                                onError: (Int?, ErrorResponseError?) -> Boolean = { _, _ -> false }) {
        notBusy()
        when (exception) {
            is HttpException -> {
                try {
                    val error = GsonBuilder().create().fromJson(
                            exception.response().errorBody()?.charStream(),
                            ErrorResponse::class.java)
                    if (!onError(exception.code(), error?.error)) {
                        if (error?.error != null) {
                            errorRelay.accept(error.error)
                        }
                    }
                } catch (e: Throwable) {
                    Timber.e(e)
                    if (!onError(exception.code(), null)) {
                        exceptionRelay.accept(e)
                    }
                }
            }
            else -> {
                if (!onError(null, null)) {
                    Timber.e(exception)
                    exceptionRelay.accept(exception)
                }
            }
        }
    }

    private fun busy() {
        busyRelay.accept(BusyState(inFlightCount.incrementAndGet() > 0))
    }

    private fun notBusy() {
        busyRelay.accept(BusyState(inFlightCount.decrementAndGet() > 0))
    }
}
