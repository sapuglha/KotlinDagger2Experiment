#!/usr/bin/env kotlinc -script

import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.experimental.and

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

try {
    println("${args[0]} -> ${obfuscate(args[0])}")
} catch (ex: Throwable) {
    println("usage: ./obfuscate.kts [text]")
}