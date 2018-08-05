package com.robotsandpencils.kotlindaggerexperiement.app.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Trace
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.robotsandpencils.kotlindaggerexperiement.R
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.experimental.and


/**
 * Hide the soft keyboard for an activity
 *
 * @param this@hideSoftKeyboard the activity that has the focus
 */
fun Activity.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val windowToken = currentFocus?.windowToken ?: return
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

val Context.isRunningTest: Boolean by lazy {
    try {
        Class.forName("android.support.test.espresso.Espresso")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

/**
 * Function to allow a trace section to be created to do timing
 * of how long a section of code might take to execute at runtime. Trace output
 * can be viewed my using Systrace.
 */
inline fun <T> trace(sectionName: String, body: () -> T): T {
    Trace.beginSection(sectionName)
    return try {
        body()
    } finally {
        Trace.endSection()
    }
}

/**
 * Function to initialize a recyclerview a bit more easily.
 */
inline fun RecyclerView.initializeWithLinearLayout(separators: Boolean = false, body: RecyclerView.() -> Unit) {

    layoutManager = LinearLayoutManager(context)

    body()

    // Disable item change animations
    val itemAnimator = itemAnimator as SimpleItemAnimator
    itemAnimator.supportsChangeAnimations = false

    if (separators) {
        // add a simple line separator to the recyclerview
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.horizontal_divider)!!)
        addItemDecoration(dividerItemDecoration)
    }
}

fun TextView.setHtmlText(html: String?) {
    when (html) {
        null -> {
            this.text = null
        }
        else -> {
            this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(html)
            }

            this.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}

fun String.decodeBase64(): String = String(Base64.decode(this, Base64.DEFAULT), StandardCharsets.UTF_8)
fun String.encodeBase64(): String = Base64.encodeToString(this.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

fun String.normalizeSpace(): String = this.replace(Regex("\\s+"), " ").trim()

/*
 * see https://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/jetty-util/src/main/java/org/eclipse/jetty/util/security/Password.java
 */
fun obfuscate(string: String): String {
    val buf = StringBuilder()
    val b = string.toByteArray(StandardCharsets.UTF_8)

    buf.append("OBF:")
    for (i in b.indices) {
        val b1 = b[i]
        val b2 = b[b.size - (i + 1)]
        if (b1 < 0 || b2 < 0) {
            val i0 = (0xff.toByte() and b1) * 256 + (0xff.toByte() and b2)
            val x = Integer.toString(i0, 36).toLowerCase(Locale.ENGLISH)
            buf.append("U0000", 0, 5 - x.length)
            buf.append(x)
        } else {
            val i1 = 127 + b1.toInt() + b2.toInt()
            val i2 = 127 + b1 - b2
            val i0 = i1 * 256 + i2
            val x = Integer.toString(i0, 36).toLowerCase(Locale.ENGLISH)

            buf.append("000", 0, 4 - x.length)
            buf.append(x)
        }

    }
    return buf.toString()

}

/*
 * see https://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/jetty-util/src/main/java/org/eclipse/jetty/util/security/Password.java
 */
fun deobfuscate(string: String) =
        if (string.startsWith("OBF:")) {
            val s = string.substring(4)
            val b = ByteArray(s.length / 2)
            var l = 0
            var i = 0
            while (i < s.length) {
                if (s[i] == 'U') {
                    i++
                    val x = s.substring(i, i + 4)
                    val i0 = Integer.parseInt(x, 36)
                    val bx = (i0 shr 8).toByte()
                    b[l++] = bx
                } else {
                    val x = s.substring(i, i + 4)
                    val i0 = Integer.parseInt(x, 36)
                    val i1 = i0 / 256
                    val i2 = i0 % 256
                    val bx = ((i1 + i2 - 254) / 2).toByte()
                    b[l++] = bx
                }
                i += 4
            }

            String(b, 0, l, StandardCharsets.UTF_8)
        } else {
            string
        }
