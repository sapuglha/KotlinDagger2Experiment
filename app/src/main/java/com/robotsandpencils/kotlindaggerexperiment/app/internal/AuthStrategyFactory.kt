package com.robotsandpencils.kotlindaggerexperiment.app.internal

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jakewharton.rxrelay2.ReplayRelay
import com.mtramin.rxfingerprint.EncryptionMethod
import com.mtramin.rxfingerprint.RxFingerprint
import com.mtramin.rxfingerprint.data.FingerprintDecryptionResult
import com.mtramin.rxfingerprint.data.FingerprintResult
import com.robotsandpencils.kotlindaggerexperiment.App
import com.robotsandpencils.kotlindaggerexperiment.R
import com.robotsandpencils.kotlindaggerexperiment.app.managers.PreferencesManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Provides a way to build the correct AuthStrategy based on the user's sign in preferences.
 */

class AuthStrategyFactory(val app: App, val preferencesManager: PreferencesManager) {

    val authenticationPreference: AuthenticationPreference
        get() =
            AuthenticationPreference.valueOf(preferencesManager.getString(AUTH_PREFERENCE, AuthenticationPreference.RememberMe.toString())
                    ?: AuthenticationPreference.RememberMe.toString())

    enum class AuthenticationPreference {
        RememberMe,
        TouchLogin,
        RequirePassword
    }

    interface AuthStrategy {
        var authToken: String?
        var refreshToken: String?
        val isWaitingForFingerprint: Boolean
        var authenticatedUser: String?
        fun performFingerprintAuthentication(context: Context): Observable<FingerprintDecryptionResult>?
        fun cancelFingerprintAuthentication()
        fun clear()
    }

    private inner class RememberMeAuthStrategy : AuthStrategy {

        override var authToken: String?
            get() = preferencesManager.getString(AUTH_TOKEN_KEY, null)
            set(authToken) {
                preferencesManager.putString(AUTH_TOKEN_KEY, authToken)
            }

        override val isWaitingForFingerprint: Boolean
            get() = false

        override var refreshToken: String?
            get() = preferencesManager.getString(REFRESH_TOKEN_KEY, null)
            set(refreshToken) {
                preferencesManager.putString(REFRESH_TOKEN_KEY, refreshToken)
            }

        override var authenticatedUser: String?
            get() = preferencesManager.getString(AUTHENTICATED_USER_KEY, null)
            set(userId) {
                preferencesManager.putString(AUTHENTICATED_USER_KEY, userId)
            }

        override fun performFingerprintAuthentication(context: Context): Observable<FingerprintDecryptionResult>? = null

        override fun cancelFingerprintAuthentication() {}

        override fun clear() {
            preferencesManager.remove(REFRESH_TOKEN_KEY)
            preferencesManager.remove(AUTH_TOKEN_KEY)
            preferencesManager.remove(AUTHENTICATED_USER_KEY)
        }
    }

    private inner class TouchLoginAuthStrategy internal constructor(private val context: Context) : AuthStrategy {

        private var _authToken: String? = null
        private var _refreshToken: String? = null

        override var authToken: String?
            get() = _authToken
            @Synchronized set(value) {
                _authToken = value
                encryptTokens()
            }

        override var refreshToken: String?
            get() = _refreshToken
            set(value) {
                _refreshToken = value
                encryptTokens()

            }

        override var isWaitingForFingerprint = false
            private set

        private var encryptedTokens: String? = null
        private var fingerprintDisposable: Disposable? = null

        private val authJson: String
            get() {
                val tokenInfo = JsonObject()
                tokenInfo.addProperty(AUTH_TOKEN, authToken)
                tokenInfo.addProperty(REFRESH_TOKEN, refreshToken)
                tokenInfo.addProperty(AUTHENTICATED_USER, authenticatedUser)
                return tokenInfo.toString()
            }

        override var authenticatedUser: String?
            get() = preferencesManager.getString(AUTHENTICATED_USER_KEY, null)
            set(userId) {
                preferencesManager.putString(AUTHENTICATED_USER_KEY, userId)
            }

        init {
            encryptedTokens = preferencesManager.getString("encrypted_tokens", null)

            if (encryptedTokens != null) {
                Timber.d("Encrypted tokens found. Need to decrypt. Asking for fingerprint.")
                isWaitingForFingerprint = true
            }
        }

        @Synchronized
        private fun setTokensFromPlainText(decrypted: String) {
            val tokens = Gson().fromJson(decrypted, JsonObject::class.java)

            authToken = tokens.get(AUTH_TOKEN).asString
            refreshToken = tokens.get(REFRESH_TOKEN).asString
            authenticatedUser = tokens.get(AUTHENTICATED_USER).asString
        }

        override fun performFingerprintAuthentication(context: Context): Observable<FingerprintDecryptionResult> {
            val relay = ReplayRelay.create<FingerprintDecryptionResult>()

            encryptedTokens?.let { encryptedTokens ->
                fingerprintDisposable = RxFingerprint.decrypt(EncryptionMethod.RSA, context, AUTH_TOKEN_KEY_NAME, encryptedTokens)
                        .subscribe({ result ->
                            when (result.result) {
                                FingerprintResult.FAILED -> {
                                    isWaitingForFingerprint = false
                                    Timber.d("Fingerprint not recognized during encrypt.")
                                    relay.accept(result)
                                }
                                FingerprintResult.HELP -> {
                                    Timber.d("Help during fingerprint decrypt: %s", result.message)
                                    relay.accept(result)
                                }
                                FingerprintResult.AUTHENTICATED -> {
                                    isWaitingForFingerprint = false
                                    Timber.d("Successfully decrypted: %s to %s", encryptedTokens, result.decrypted)
                                    setTokensFromPlainText(result.decrypted)
                                    relay.accept(result)
                                }
                                else -> {
                                    Timber.e("Unexpected Return Value")
                                }
                            }
                        }, { throwable ->
                            Timber.e(throwable, "Error decrypting.")
                            relay.accept(FingerprintDecryptionResult(FingerprintResult.FAILED,
                                    context.getString(R.string.auth_factory_fingerprint_decryption_failed), null))
                        })
            }

            return relay
        }

        override fun cancelFingerprintAuthentication() {
            fingerprintDisposable?.dispose()
            isWaitingForFingerprint = false
        }

        @SuppressLint("CheckResult")
        private fun encryptTokens() {

            if (hasMissingValues()) return

            // Using RSA encryption, the user is not asked to provide their fingerprint on encryption,
            // only decryption. So setting the authtoken will store the encrypted form into shared preferences
            // and keep the unencrypted form in memory.

            val json = authJson

            RxFingerprint.encrypt(EncryptionMethod.RSA, context, AUTH_TOKEN_KEY_NAME, json)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ encryptionResult ->
                        when (encryptionResult.result) {
                            FingerprintResult.FAILED -> Timber.d("Fingerprint not recognized during encrypt.")
                            FingerprintResult.HELP -> Timber.d("Help during fingerprint encrypt: %s", encryptionResult.message)
                            FingerprintResult.AUTHENTICATED -> {
                                Timber.d("Successfully encrypted: %s to %s", json, encryptionResult.encrypted)
                                preferencesManager.putString("encrypted_tokens", encryptionResult.encrypted)
                            }
                            else -> {
                                Timber.e("Unexpected fingerprint result.")
                            }
                        }
                    }, { throwable -> Timber.e(throwable, "Error encrypting.") })
        }

        private fun hasMissingValues(): Boolean =
                authToken == null || refreshToken == null || authenticatedUser == null

        override fun clear() {
            authToken = null
            refreshToken = null
            preferencesManager.remove("encrypted_tokens")
            preferencesManager.remove(AUTHENTICATED_USER_KEY)
        }

    }

    private inner class RequirePasswordAuthStrategy : AuthStrategy {

        override var authToken: String? = null
        override var refreshToken: String? = null
        override var authenticatedUser: String? = null

        override val isWaitingForFingerprint: Boolean = false

        override fun performFingerprintAuthentication(context: Context): Observable<FingerprintDecryptionResult>? =
                null

        override fun cancelFingerprintAuthentication() {
            // not required
        }

        override fun clear() {
            authToken = null
            refreshToken = null
        }
    }

    init {
        app.userComponent.inject(this)
    }

    fun build(context: Context): AuthStrategy {
        val setting = authenticationPreference

        return when (setting) {
            AuthStrategyFactory.AuthenticationPreference.RememberMe -> RememberMeAuthStrategy()
            AuthStrategyFactory.AuthenticationPreference.TouchLogin -> TouchLoginAuthStrategy(context)
            AuthStrategyFactory.AuthenticationPreference.RequirePassword -> RequirePasswordAuthStrategy()
        }
    }

    fun updateAuthenticationPreference(context: Context, authenticationPreference: AuthenticationPreference,
                                       currentStrategy: AuthStrategy): AuthStrategy {
        preferencesManager.putString(AUTH_PREFERENCE, authenticationPreference.toString())

        val newStrategy = build(context)

        newStrategy.authToken = currentStrategy.authToken
        newStrategy.refreshToken = currentStrategy.refreshToken
        newStrategy.authenticatedUser = currentStrategy.authenticatedUser

        return newStrategy
    }

    fun hasAuthenticationPreference(): Boolean = preferencesManager.contains(AUTH_PREFERENCE)

    fun clearAuthenticationPreference() {
        preferencesManager.remove(AUTH_PREFERENCE)
    }

    companion object {
        private val AUTH_TOKEN_KEY_NAME = "auth_token_key"
        val AUTH_TOKEN = "authToken"
        val REFRESH_TOKEN = "refreshToken"
        val AUTHENTICATED_USER = "authenticatedUser"

        private val AUTH_TOKEN_KEY = "auth_token"
        private val REFRESH_TOKEN_KEY = "refresh_token"
        private val AUTHENTICATED_USER_KEY = "authenticated_user"

        private val AUTH_PREFERENCE = "auth_type_setting"
    }
}
