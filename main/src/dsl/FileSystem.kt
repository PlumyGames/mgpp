package io.github.liplum.dsl

import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import java.io.File
import java.io.InputStream
import java.nio.file.Files

/**
 * Copy data from this input stream to [file].
 * @receiver Caller has responsibility to close this stream
 */
internal
fun InputStream.copyTo(file: File) {
    file.outputStream().use {
        this.copyTo(it)
    }
}

internal
fun InputStream.copyToTmpAndMove(dest: File) {
    val tmp = Files.createTempFile(dest.name, null).toFile()
    this.use {
        it.copyTo(tmp)
    }
    dest.ensureParentDir()
    tmp.renameTo(dest)
    // ignore the error when deleting the temp file
    runCatching {
        tmp.delete()
    }
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
fun <T> findFileInOrder(getFile: T.() -> File, vararg files: T): T {
    for ((i, file) in files.withIndex()) {
        return if (getFile(file).exists()) file
        else if (i >= files.size - 1) file
        else continue
    }
    return files.last()
}

internal
fun findFileInOrder(vararg files: RegularFile): RegularFile {
    return findFileInOrder({ asFile }, *files)
}

internal
fun findFileInOrder(vararg files: File): File {
    return findFileInOrder({ this }, *files)
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

fun Task.createSymbolicLinkOrCopy(
    link: File,
    target: File,
    overwrite: Boolean = false,
) {
    if (!overwrite && link.exists()) return
    try {
        Files.createSymbolicLink(link.toPath(), target.toPath())
        logger.info("Created symbolic link: $target -> $link.")
    } catch (error: Exception) {
        logger.error("Cannot create symbolic link: $target -> $link, because $error. Fallback to copy file.")
        target.copyTo(link, overwrite)
        logger.info("Copied: $target -> $link.")
    }
}