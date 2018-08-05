package com.robotsandpencils.kotlindaggerexperiment.app.internal

import com.robotsandpencils.kotlindaggerexperiment.app.repositories.AuthRepository
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class AuthenticationInterceptor(private val authRepository: AuthRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        val c = chain!!
        var resp: Response
        if (c.request().headers("Authorization").isNotEmpty()
                && authRepository.hasAuthToken()) {
            Timber.d("Modifying request with current authorization token")
            resp = c.proceed(c.request().newBuilder()
                    .header("Authorization", "Bearer ${authRepository.authToken}")
                    .build())
            if (resp.code() == 401) {

                val length = resp.peekBody(1).contentLength()
                if (length > 0L) {
                    Timber.d("Alternate 401 error handled.")
                    return resp
                }

                Timber.d("Authorization failed, refreshing token")
                if (authRepository.tokenRefresh()) {
                    Timber.d("Token refreshed, retrying request")
                    resp = c.proceed(c.request().newBuilder()
                            .header("Authorization", "Bearer ${authRepository.authToken}")
                            .build())
                }
            }
        } else {
            resp = c.proceed(c.request())
        }
        return resp
    }

}
