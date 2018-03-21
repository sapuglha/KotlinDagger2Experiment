package com.robotsandpencils.kotlindaggerexperiement.net.xkcd

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Get Xkcd Comics: See https://xkcd.com/json.html
 */
interface XkcdAPI {
    @GET("/info.0.json")
    fun getLatestComic(): Observable<XkcdResponse>

    @GET("/{num}/info.0.json")
    fun getComic(@Path("num") num: Int): Observable<XkcdResponse>
}