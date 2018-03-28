package com.robotsandpencils.kotlinexperiment.data.api.xkcd

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Get Xkcd Comics: See https://xkcd.com/json.html
 */
interface ComicApi {
    @GET("/info.0.json")
    fun getLatestComic(): Observable<XkcdResponse>

    @GET("/{num}/info.0.json")
    fun getComic(@Path("num") num: Int): Observable<XkcdResponse>
}