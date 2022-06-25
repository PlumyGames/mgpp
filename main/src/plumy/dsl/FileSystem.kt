package plumy.dsl

import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * Copy data from this input stream to [file].
 * @receiver Caller has responsibility to close this stream
 */
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
fun URL.copyTo(file: File): File {
    file.parentFile.mkdirs()
    this.openStream().use {
        it.copyTo(file)
    }
    return file
}

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

fun File.mapTo(folder: File, overwrite: Boolean = false) {
    val target = folder.resolve(name)
    folder.mkdirs()
    if (!target.exists() || overwrite)
        this.copyTo(target)
}

fun Iterable<File>.mapFilesTo(
    folder: File, overwrite: Boolean = true,
) {
    folder.mkdirs()
    forEach { it.copyTo(folder.resolve(it.name), overwrite) }
}