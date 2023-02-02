@file:JvmMultifileClass
@file:JvmName("DslKt")

package io.github.liplum.dsl

import java.io.File
import java.io.OutputStream

internal
fun Any?.atLeastEqualsOne(vararg possibilities: Any?): Boolean {
    for (possibility in possibilities) {
        if (this == possibilities) return true
    }
    return false
}
internal
operator fun Appendable.plusAssign(s: String) {
    this.append(s)
}

internal
operator fun OutputStream.plusAssign(str: String) {
    this.write(str.toByteArray())
}

internal
operator fun OutputStream.plusAssign(bytes: ByteArray) {
    this.write(bytes)
}

internal
fun OutputStream.line() {
    this += "\n"
}

internal
fun String.packageName2FileName(relativeTo: File): File {
    val subPacks = this.split(".")
    var res = relativeTo
    for (subPack in subPacks) {
        res = res.resolve(subPack)
    }
    return res
}

internal
fun String.packageAndClassName(): Pair<String, String> {
    if (isEmpty()) return Pair("", "")
    val beforeClzName = this.lastIndexOf(".")
    if (beforeClzName < 0) return Pair("", this)
    if (beforeClzName == 0) return Pair("", substring(1, length))
    return Pair(substring(0, beforeClzName), substring(beforeClzName + 1, length))
}

internal
fun String.qualified2FileName(
    relativeTo: File,
    extension: String = "java",
): File {
    val subPacks = this.split(".")
    var res = relativeTo
    for ((i, subPack) in subPacks.withIndex()) {
        res = if (i < subPacks.size - 1)
            res.resolve(subPack)
        else
            res.resolve("$subPack.$extension")
    }
    return res
}

internal
fun String.simpleName() =
    split('.').last()

internal
operator fun String.times(times: Int) =
    this.repeat(times)

internal
operator fun StringBuilder.plusAssign(c: Char) {
    this.append(c)
}

internal
fun linkString(separator: String, vararg strings: String?) =
    linkString(separator, strings.toList())

internal
fun linkString(separator: String, strings: List<String?>): String {
    val sb = StringBuilder()
    for ((i, str) in strings.withIndex()) {
        if (str.isNullOrBlank()) continue
        if (i != 0) {
            sb.append(separator)
        }
        sb.append(str)
    }
    return sb.toString()
}
