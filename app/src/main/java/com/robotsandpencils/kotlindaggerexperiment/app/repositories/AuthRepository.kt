package com.robotsandpencils.kotlindaggerexperiment.app.repositories


import android.content.Context
import com.jakewharton.rxrelay2.PublishRelay
import com.mtramin.rxfingerprint.data.FingerprintDecryptionResult
import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlindaggerexperiment.app.internal.AuthStrategyFactory
import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
import io.reactivex.Observable
import timber.log.Timber

/**
 * Repository for authentication information.
 */
class AuthRepository(private val app: App,
                     preferencesManager: PreferencesManager) : BaseRepository() {

    private val signOutRelay = PublishRelay.create<Boolean>()

    private val authStrategyFactory: AuthStrategyFactory = AuthStrategyFactory(app, preferencesManager)
    private var authStrategy: AuthStrategyFactory.AuthStrategy = authStrategyFactory.build(app)


    var authToken: String?
        get() = authStrategy.authToken
        set(token) {
            authStrategy.authToken = token
        }

    var refreshToken: String?
        get() = authStrategy.refreshToken
        set(token) {
            authStrategy.refreshToken = token
        }

    val isWaitingForFingerprint: Boolean
        get() = authStrategy.isWaitingForFingerprint

    /**
     * Return an observable that signals whether the user has been
     * signed out of the system.
     *
     * @return a stream of boolean values indicating if the user is signed out
     */
    val signedOut: Observable<Boolean>
        get() = signOutRelay

    val authenticatedUserId: String?
        get() = authStrategy.authenticatedUser

    var authenticationPreference: AuthStrategyFactory.AuthenticationPreference
        get() = authStrategyFactory.authenticationPreference
        set(authenticationPreference) {
            authStrategy.let { oldStrategy ->
                authStrategy = authStrategyFactory.updateAuthenticationPreference(app, authenticationPreference, oldStrategy)
            }
        }

    init {
        authStrategy = authStrategyFactory.build(app)
    }

    fun hasAuthToken(): Boolean =
            !(authToken.isNullOrBlank() || refreshToken.isNullOrBlank())

    fun performFingerprintAuthentication(context: Context): Observable<FingerprintDecryptionResult>? =
            authStrategy.performFingerprintAuthentication(context)

    fun cancelFingerprintAuthentication() {
        authStrategy.cancelFingerprintAuthentication()
    }

    /**
     * Sign out the user, delete any tokens, and fire a signal to any listeners.
     */
    fun signOut() {
        authStrategy.clear()
        authStrategyFactory.clearAuthenticationPreference()

        app.resetUserComponent()

        Timber.d("User was signed out.")
        signOutRelay.accept(true)
    }

    fun hasAuthenticationPreference(): Boolean = authStrategyFactory.hasAuthenticationPreference()

    fun clearAuthenticationPreference() {
        authStrategyFactory.clearAuthenticationPreference()
    }

    fun setAuthenticatedUser(authenticatedUser: String) {
        authStrategy.authenticatedUser = authenticatedUser
    }

    fun tokenRefresh(): Boolean {
        TODO("Token Refresh is not implemented")
    }
}
