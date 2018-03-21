package com.robotsandpencils.kotlindaggerexperiement.tests

import android.support.test.espresso.IdlingRegistry
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import com.robotsandpencils.kotlindaggerexperiement.base.BaseTest
import com.robotsandpencils.kotlindaggerexperiement.robots.app
import com.squareup.picasso.PicassoIdlingResource
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

class ComicTest : BaseTest() {

    val picassoIdlingResource = PicassoIdlingResource()

    @Before
    override fun before() {
        super.before()

        IdlingRegistry.getInstance().register(picassoIdlingResource)

        ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(picassoIdlingResource)
    }

    @Test
    fun shouldShowError() {

        assets {
            get(Matchers.containsString("info"), 200, "samples/info-200-real-estate.json")
        }

        app {
            dashboard {
                assertTitle("Real Estate")
                assertImageUrl("https://imgs.xkcd.com/comics/real_estate.png")

                tapPreviousComic()

                // Will get a 404 and will not change the title
                assertTitle("Real Estate")
            }
        }
    }

    @Test
    fun shouldShowPreviousComic() {

        assets {
            get(Matchers.containsString("info"), 200, "samples/info-200-real-estate.json")
            get(Matchers.containsString("1893/info"), 200, "samples/info-200-1893.json")
        }

        app {
            dashboard {
                assertTitle("Real Estate")
                assertImageUrl("https://imgs.xkcd.com/comics/real_estate.png")

                tapPreviousComic()

                // Will not error because there is are two HTTP request queued.
                assertTitle("Thread")
                assertImageUrl("https://imgs.xkcd.com/comics/thread.png")
            }
        }
    }

}