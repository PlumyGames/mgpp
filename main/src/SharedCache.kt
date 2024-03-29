package io.github.liplum.mindustry

import java.io.File
import io.github.liplum.dsl.ensureParentDir
import io.github.liplum.dsl.fromJson
import io.github.liplum.dsl.gson
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import kotlin.math.absoluteValue
import kotlin.math.log


private
const val lockFileEx = "lock.json"

data class DownloadLock(
    /**
     * It's changed when the mod is updated or network error.
     */
    val lastUpdateTimestamp: Long
)

data class LatestCache(
    var name: String,
    var lastValue: String,
    var lastUpdatedTimeStamp: Long,
)

object SharedCache {
    private fun resolveGradleUserHome(): File {
        val gradleUserHome: String? = System.getProperty("GRADLE_USER_HOME")
        return if (gradleUserHome != null) {
            File(gradleUserHome)
        } else {
            val userHome = System.getProperty("user.home")
            File(userHome).resolve(".gradle")
        }
    }

    val cacheDir: File
        get() = resolveGradleUserHome().resolve("mindustry-mgpp")

    val modsDir: File
        get() = cacheDir.resolve("mods")

    val gamesDir: File
        get() = cacheDir.resolve("games")


    internal
    inline fun Project.fetchLatest(
        type: String,
        namespace: String,
        outOfDate: Long = R.outOfDataDuration,
        fetch: () -> String,
    ): String {
        val jsonFile = SharedCache.cacheDir.resolve("$namespace.json").ensureParentDir()
        val json = if (jsonFile.exists())
            jsonFile.readText()
        else "[]"
        val all = runCatching {
            gson.fromJson<Array<LatestCache>>(json).toMutableList()
        }.getOrDefault(ArrayList())
        val cache = all.find { it.name == type }
        val res: String
        var changed = false
        if (cache != null) {
            val curTime = System.currentTimeMillis()
            if (curTime - cache.lastUpdatedTimeStamp < outOfDate.absoluteValue) {
                res = cache.lastValue
            } else { // out of date
                res = fetch()
                cache.lastValue = res
                cache.lastUpdatedTimeStamp = System.currentTimeMillis()
                changed = true
            }
        } else {
            res = fetch()
            all.add(
                LatestCache(
                    name = type,
                    lastValue = res,
                    lastUpdatedTimeStamp = System.currentTimeMillis()
                )
            )
            changed = true
        }
        if (changed) {
            all.sortBy { it.name }
            jsonFile.writeText(gson.toJson(all))
        }
        return res
    }

    internal
    fun updateDownloadLock(
        lockFile: File,
        newTimestamp: Long = System.currentTimeMillis(),
        logger: Logger? = null,
    ) {
        val infoFi = File("$lockFile.$lockFileEx")
        if (infoFi.isDirectory) {
            infoFi.deleteRecursively()
        }
        val meta = DownloadLock(lastUpdateTimestamp = newTimestamp)
        val json = gson.toJson(meta)
        try {
            infoFi.writeText(json)
        } catch (e: Exception) {
            logger?.warn("Failed to write into \"info.json\"", e)
        }
    }

    internal
    fun checkUpdateToDate(
        lockFile: File,
        outOfDate: Long = R.outOfDataDuration,
        logger: Logger? = null,
    ): Boolean {
        val infoFi = File("$lockFile.$lockFileEx")
        if (!lockFile.exists()) {
            if (infoFi.exists()) infoFi.delete()
            return false
        }
        val meta = tryReadAndUpdateDownloadLock(infoFi, logger = logger)
        val curTime = System.currentTimeMillis()
        return curTime - meta.lastUpdateTimestamp < outOfDate
    }

    internal
    fun tryReadAndUpdateDownloadLock(
        infoFile: File,
        logger: Logger? = null,
    ): DownloadLock {
        fun writeAndGetDefault(): DownloadLock {
            val meta = DownloadLock(lastUpdateTimestamp = System.currentTimeMillis())
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

    fun cleanCache() {
        val dir = cacheDir
        if (dir.isDirectory) {
            dir.deleteRecursively()
        }
    }
}