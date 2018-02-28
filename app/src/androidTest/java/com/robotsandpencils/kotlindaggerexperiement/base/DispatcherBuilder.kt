package com.robotsandpencils.kotlindaggerexperiement.base

import org.hamcrest.Matcher
import java.util.*


class DispatcherBuilder {
    val assets = LinkedList<Asset>()

    fun method(method: String, path: Matcher<String>, status: Int, asset: String) =
            assets.add(Asset(method, path, status, asset))

    fun get(path: Matcher<String>, status: Int, asset: String) =
            method("GET", path, status, asset)

    fun put(path: Matcher<String>, status: Int, asset: String) =
            method("PUT", path, status, asset)

    fun post(path: Matcher<String>, status: Int, asset: String) =
            method("POST", path, status, asset)

    fun delete(path: Matcher<String>, status: Int, asset: String) =
            method("DELETE", path, status, asset)
}

data class Asset(val method: String, val path: Matcher<String>, val status: Int, val asset: String)
