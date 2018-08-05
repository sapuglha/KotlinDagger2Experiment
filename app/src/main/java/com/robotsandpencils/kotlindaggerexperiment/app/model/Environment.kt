package com.robotsandpencils.kotlindaggerexperiment.app.model

data class Environment(
        val name: String,
        val baseUrl: String,
        val apiKey: String,
        val clientSecret: String,
        val analyticsAppId: String
) {

    companion object {
        val EMPTY = Environment(
                "Null Server",
                "https://nosuchdevice.com",
                "BAD_API_KEY",
                "BAD_SECRET",
                "BAD_APP_ID"
        )

        fun createTestEnvironment(testBaseUrl: String): Environment =
                Environment(
                        "Test Server",
                        testBaseUrl,
                        "TEST_API_KEY",
                        "TEST_SECRET",
                        "TEST_ANDROID_APP_ID"
                )
    }
}
