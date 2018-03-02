package com.robotsandpencils.kotlindaggerexperiement.app.managers


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.robotsandpencils.kotlindaggerexperiement.app.extensions.isRunningTest
import com.securepreferences.SecurePreferences
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

/**
 * Manages Preferences
 */
@com.robotsandpencils.kotlindaggerexperiment.OpenClassOnDebug
class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences

    init {
        preferences = SecurePreferences(context, generatePassword(context),
                if (context.isRunningTest) "testPreferences" else "mainPreferences")
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun generatePassword(context: Context): String {

        val sb = StringBuilder()

        val packageManager = context.packageManager
        try {
            val packageName = context.packageName
            @SuppressLint("PackageManagerGetSignatures")
            val packageInfo: PackageInfo =
                    packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)

            sb.append(packageName)

            packageInfo.signatures.forEach { signature ->
                // get the X.509 certificate
                val rawCert = signature.toByteArray()
                val certStream = ByteArrayInputStream(rawCert)

                try {
                    val certFactory = CertificateFactory.getInstance("X509")
                    val x509Cert = certFactory.generateCertificate(certStream) as X509Certificate

                    sb.append(x509Cert.serialNumber)
                } catch (e: CertificateException) {
                    Timber.e(e)
                }

            }

        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }

        return sb.toString()
    }

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).commit()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
            preferences.getBoolean(key, defaultValue)

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun putString(key: String, value: String?) {
        preferences.edit().putString(key, value).commit()
    }

    fun getString(key: String, defaultValue: String?): String? =
            preferences.getString(key, defaultValue)

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun remove(key: String) {
        preferences.edit().remove(key).commit()
    }

    fun putFloat(key: String, f: Float) {
        preferences.edit().putFloat(key, f).apply()
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return preferences.getFloat(key, defaultValue)
    }

    fun putDate(key: String, date: Date) {
        preferences.edit().putLong(key, date.time).apply()
    }

    fun getDate(key: String, defaultDate: Date): Date =
            if (preferences.contains(key)) {
                val time = preferences.getLong(key, 0)
                Date(time)
            } else {
                defaultDate
            }

    fun removeAll(prefix: String) {
        val edit = preferences.edit()
        preferences.all.keys
                .filter { it.startsWith(prefix) }
                .forEach { edit.remove(it) }
        edit.apply()
    }

    operator fun contains(key: String): Boolean {
        return preferences.contains(key)
    }
}
