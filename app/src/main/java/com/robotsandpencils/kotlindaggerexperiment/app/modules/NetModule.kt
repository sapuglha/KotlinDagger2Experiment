package com.robotsandpencils.kotlindaggerexperiment.app.modules

import com.google.gson.Gson
import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlindaggerexperiment.BuildConfig
import com.robotsandpencils.kotlindaggerexperiment.R
import com.robotsandpencils.kotlindaggerexperiment.app.extensions.deobfuscate
import com.robotsandpencils.kotlindaggerexperiment.app.internal.AuthenticationInterceptor
import com.robotsandpencils.kotlindaggerexperiment.app.managers.EnvironmentManager
import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
import com.robotsandpencils.kotlindaggerexperiment.app.model.Environment
import com.robotsandpencils.kotlindaggerexperiment.app.model.Environments
import com.robotsandpencils.kotlindaggerexperiment.app.repositories.AuthRepository
import com.robotsandpencils.kotlinexperiment.data.api.xkcd.ComicApi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

@Module
open class NetModule {

    @Provides
    @UserScope
    open fun provideEnvironment(environments: Environments, preferencesManager: PreferencesManager): Environment =
            selectEnvironment(environments, preferencesManager)

    private fun selectEnvironment(environments: Environments, preferencesManager: PreferencesManager): Environment {
        val defaultEnvironment =
                when (BuildConfig.BUILD_TYPE) {
                    "debug" -> {
                        "Stub Server"
                    }
                    "release" -> {
                        "Production Server"
                    }
                    else -> {
                        "Stub Server"
                    }
                }

        val selectedEnvironment = preferencesManager.getString("selected_environment", defaultEnvironment)

        var environment = environments.environments.find { it.name == selectedEnvironment }

        if (environment == null) {
            // This is sure to cause some nice SSL errors that will hopefully be obvious and point
            // out a problem in creation of the environment
            environment = Environment.EMPTY
        }

        return environment
    }

    @Provides
    @UserScope
    fun environmentManager(app: App, environments: Environments, preferencesManager: PreferencesManager) =
            EnvironmentManager(app, environments, preferencesManager)

    @Provides
    @UserScope
    fun provideEnvironments(app: App): Environments {
        val environmentsJson = app.resources.openRawResource(R.raw.environments).bufferedReader().use { it.readText() }
        val environments = Gson().fromJson(environmentsJson, Environments::class.java)

        Timber.e("Environments: $environments")

        return Environments(environments.environments.map {
            Environment(it.name, it.baseUrl, deobfuscate(it.apiKey), deobfuscate(it.clientSecret), it.analyticsAppId)
        })
    }

    @Provides
    @UserScope
    fun provideAuthInterceptor(authRepository: AuthRepository)
            : AuthenticationInterceptor = AuthenticationInterceptor(authRepository)

    @Provides
    @UserScope
    fun provideHttpClient(app: App,
                          authInterceptor: AuthenticationInterceptor): OkHttpClient {
        val level =
                if (com.robotsandpencils.kotlinexperiment.data.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(level))
                .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .cache(Cache(File(app.cacheDir.absolutePath, "HttpCache"), CACHE_MAX_SIZE))
                .build()
    }

    @Provides
    @UserScope
    fun provideXkcdApi(okHttpClient: OkHttpClient, env: Environment): ComicApi {
        return Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(env.baseUrl)
                .build()
                .create(ComicApi::class.java)
    }

    companion object {
        const val READ_TIMEOUT_MS: Long = 30000
        const val WRITE_TIMEOUT_MS: Long = 10000
        const val CONNECT_TIMEOUT_MS: Long = 10000
        const val CACHE_MAX_SIZE: Long = 100 * 1024 * 1024 // 100 MB

        // Hook for providing test environment
        var isUnderTest = false
        lateinit var testBaseUrl: String
    }
}