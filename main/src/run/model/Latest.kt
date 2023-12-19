package io.github.liplum.mindustry

import io.github.liplum.dsl.ensureParentDir
import io.github.liplum.dsl.fromJson
import io.github.liplum.dsl.gson
import org.gradle.api.logging.Logger
import java.io.File
import kotlin.math.absoluteValue


internal
const val infoX = "info.json"

data class GihHubDownloadTrack(
    /**
     * It's changed when the mod is updated or network error.
     */
    val lastUpdateTimestamp: Long
)

internal
fun updateGitHubDownloadTrack(
    lockFile: File,
    newTimestamp: Long = System.currentTimeMillis(),
    logger: Logger? = null,
) {
    val infoFi = File("$lockFile.$infoX")
    if (infoFi.isDirectory) {
        infoFi.deleteRecursively()
    }
    val meta = GihHubDownloadTrack(lastUpdateTimestamp = newTimestamp)
    val json = gson.toJson(meta)
    try {
        infoFi.writeText(json)
    } catch (e: Exception) {
        logger?.warn("Failed to write into \"info.json\"", e)
    }
}

internal
fun isUpdateToDate(
    lockFile: File
): Boolean {
    val infoFi = File("$lockFile.$infoX")
    if (!lockFile.exists()) {
        if (infoFi.exists()) infoFi.delete()
        return false
    }
    val meta = tryReadGitHubDownloadTrack(infoFi)
    val curTime = System.currentTimeMillis()
    // TODO: Configurable out-of-date time
    return curTime - meta.lastUpdateTimestamp < R.outOfDataDuration.absoluteValue
}

internal
fun tryReadGitHubDownloadTrack(
    infoFile: File,
    logger: Logger? = null,
): GihHubDownloadTrack {
    fun writeAndGetDefault(): GihHubDownloadTrack {
        val meta = GihHubDownloadTrack(lastUpdateTimestamp = System.currentTimeMillis())
        val infoContent = gson.toJson(meta)
        try {
            infoFile.ensureParentDir().writeText(infoContent)
            logger?.info("$infoFile is created.")
        } catch (e: Exception) {
            logger?.warn("Failed to write into \"info.json\"", e)
        }
        return meta
    }
    return if (infoFile.isFile) {
        try {
            val infoContent = infoFile.readText()
            gson.fromJson(infoContent)
        } catch (e: Exception) {
            writeAndGetDefault()
        }
    } else {
        writeAndGetDefault()
    }
}
