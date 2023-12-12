@file:JvmMultifileClass
@file:JvmName("DslKt")

package io.github.liplum.dsl

import org.gradle.api.Task
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files

/**
 * Copy data from this input stream to [file].
 * @receiver Caller has responsibility to close this stream
 */
internal
fun InputStream.copyTo(file: File): File {
    file.outputStream().use {
        this.copyTo(it)
    }
    return file
}

/**
 * Copy data from this url to [file].
 * It will create the parent folder if it doesn't exist.
 */
internal
fun URL.copyTo(file: File): File {
    file.parentFile.mkdirs()
    this.openStream().use {
        it.copyTo(file)
    }
    return file
}

internal
fun File.ensureParentDir(): File {
    parentFile?.mkdirs()
    return this
}

internal
fun File.getOrCreateDir(): File {
    mkdirs()
    return this
}

internal
fun FileAt(
    vararg segments: String,
): File = StringBuilder().run {
    for ((i, seg) in segments.withIndex()) {
        append(seg)
        if (i < segments.size - 1 && !seg.endsWith(File.separator)) {
            append(File.separator)
        }
    }
    File(this.toString())
}

internal
fun File.mapTo(folder: File, overwrite: Boolean = true) {
    val target = folder.resolve(name)
    folder.mkdirs()
    if (!target.exists() || overwrite)
        this.copyTo(target, true)
}

internal
fun Iterable<File>.mapFilesTo(
    folder: File, overwrite: Boolean = true,
) {
    folder.mkdirs()
    forEach {
        val target = folder.resolve(it.name)
        if (!target.exists() || overwrite)
            it.copyTo(target, true)
    }
}

internal
fun File.getFilesRecursive() = ArrayList<File>().apply {
    forEachFilesRecursive { add(it) }
}

internal
fun File.forEachFilesRecursive(func: (File) -> Unit) {
    if (this.isDirectory) {
        this.listFiles()?.forEach {
            it.forEachFilesRecursive(func)
        }
    } else if (this.isFile) {
        func(this)
    }
}

internal
fun File.findFileInOrder(vararg files: File): File {
    if (this.exists()) return this
    for ((i, file) in files.withIndex()) {
        return if (file.exists()) file
        else if (i >= files.size - 1) file
        else continue
    }
    return files.last()
}

internal
fun findFileInOrder(vararg files: File): File {
    for ((i, file) in files.withIndex()) {
        return if (file.exists()) file
        else if (i >= files.size - 1) file
        else continue
    }
    return files.last()
}

internal
fun File.findFileInOrder(vararg files: () -> File): File {
    if (this.exists()) return this
    for ((i, file) in files.withIndex()) {
        val f = file()
        return if (f.exists()) f
        else if (i >= files.size - 1) f
        else continue
    }
    return files.last()()
}

internal
fun findFileInOrder(vararg files: () -> File): File {
    for ((i, file) in files.withIndex()) {
        val f = file()
        return if (f.exists()) f
        else if (i >= files.size - 1) f
        else continue
    }
    return files.last()()
}

fun Task.createSymbolicLinkOrCopyCache(link: File, target: File) {
    if (link.exists()) return
    try {
        Files.createSymbolicLink(link.toPath(), target.toPath())
        logger.lifecycle("Created symbolic link: $target -> $link.")
    } catch (error: Exception) {
        logger.lifecycle("Cannot create symbolic link: $target -> $link, because $error.")
        logger.lifecycle("Fallback to copy file.")
        target.copyTo(link)
        logger.lifecycle("Copied: $target -> $link.")
    }
}