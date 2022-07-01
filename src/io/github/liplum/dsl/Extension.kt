package io.github.liplum.dsl

import arc.util.serialization.JsonValue
import arc.util.serialization.JsonValue.ValueType
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

operator fun OutputStream.plusAssign(str: String) {
    this.write(str.toByteArray())
}

operator fun OutputStream.plusAssign(bytes: ByteArray) {
    this.write(bytes)
}

fun OutputStream.line() {
    this += "\n"
}

fun String.packageName2FileName(relativeTo: File): File {
    val subPacks = this.split(".")
    var res = relativeTo
    for (subPack in subPacks) {
        res = res.resolve(subPack)
    }
    return res
}

fun String.packageAndClassName(): Pair<String, String> {
    if (isEmpty()) return Pair("", "")
    val beforeClzName = this.lastIndexOf(".")
    if (beforeClzName < 0) return Pair("", this)
    if (beforeClzName == 0) return Pair("", substring(1, length))
    return Pair(substring(0, beforeClzName), substring(beforeClzName + 1, length))
}

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

fun String.simpleName() =
    split('.').last()

operator fun String.times(times: Int) =
    this.repeat(times)

operator fun StringBuilder.plusAssign(c: Char) {
    this.append(c)
}