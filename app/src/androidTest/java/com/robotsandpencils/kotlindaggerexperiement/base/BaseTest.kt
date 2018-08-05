package com.robotsandpencils.kotlindaggerexperiement.base

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.IdlingRegistry
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.robotsandpencils.kotlindaggerexperiement.TestApp
import com.robotsandpencils.kotlindaggerexperiement.TestHelper
import com.robotsandpencils.kotlindaggerexperiement.app.modules.NetModule
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.hamcrest.Matchers.containsString
import org.junit.After
import org.junit.Before
import timber.log.Timber
import java.io.ByteArrayOutputStream

open class BaseTest {

    companion object {
        val PORT = 8080
    }

    protected lateinit var server: MockWebServer
    private lateinit var okIdler: OkHttp3IdlingResource

    @Before
    open fun before() {
        startWebServer()

        val testApp = TestHelper.getApp() as TestApp
        okIdler = OkHttp3IdlingResource.create("OkHttp", testApp.userComponent.client())
        IdlingRegistry.getInstance().register(okIdler)
    }

    @After
    open fun after() {
        stopWebServer()
        val testApp = TestHelper.getApp() as TestApp
        testApp.clearComponent()

        IdlingRegistry.getInstance().unregister(okIdler)
    }

    fun assets(func: DispatcherBuilder.() -> Unit) {
        val builder = DispatcherBuilder().apply(func)

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val a = builder.assets.firstOrNull {
                    it.method.toUpperCase() == request.method &&
                            it.path.matches(request.path)
                }
                return when {
                    a == null && containsString("config/v1/info").matches(request.path) -> {
                        val stream = InstrumentationRegistry.getContext().assets.open("sample/info-200.json")
                        MockResponse().setResponseCode(200).setBody(Buffer().readFrom(stream))
                    }
                    a == null -> {
                        Timber.w("Failed to find enqueued asset for ${request.method} ${request.path}")
                        MockResponse().setResponseCode(404)
                    }
                    else -> {
                        // remove the asset so that you can test different responses to the same request
                        builder.assets.remove(a)
                        val stream = InstrumentationRegistry.getContext().assets.open(a.asset)
                        MockResponse().setResponseCode(a.status).setBody(Buffer().readFrom(stream))
                    }
                }
            }
        }
        server.setDispatcher(dispatcher)
    }

    fun take() = server.takeRequest()

    fun startWebServer() {
        server = MockWebServer()
        server.start(PORT)
        val baseUrl = server.url("/")
        NetModule.isUnderTest = true
        NetModule.testBaseUrl = baseUrl.toString()
    }

    fun stopWebServer() {
        server.shutdown()
    }

    fun requestBody(request: RecordedRequest): String {
        val out = ByteArrayOutputStream()
        request.body.copyTo(out)
        return out.toString()
    }
}
