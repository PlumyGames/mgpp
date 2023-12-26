package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.tasks.*
import java.io.File

private
const val lockFile = ".lock.json"

abstract class RunMindustryAbstract : JavaExec() {
    val gameFile = project.fileProp()
        @InputFile get
    val dataDir = project.prop<IDataDirLoc>()
        @Optional @Input get
    val startupArgs = project.stringsProp()
        @Input @Optional get
    val mods = project.configurationFileCollection()
        @InputFiles @Optional get

    init {
        mainClass.set("-jar")
    }

    protected fun loadMods(modsDir: File) {
        modsDir.mkdirs()
        val lastLock = readLock(modsDir)
        // parse lock
        if (modsDir.isDirectory) {
            // TODO: Record the mod signature, and don't always delete all.
            modsDir.deleteRecursively()
        }

        for (modFile in mods) {
            if (modFile.isFile) {
                createSymbolicLinkOrCopy(link = modsDir.resolve(modFile.name), target = modFile)
            } else {
                logger.error("Mod<$modFile> doesn't exist.")
            }
        }
    }

    /**
     * @return (existing, missing)
     */
    private fun restoreLastLock(modsDir: File): Pair<List<File>, List<File>> {
        val lastLock = readLock(modsDir)
        val files = modsDir.listFiles() ?: arrayOf()
        val restored = files.groupBy { lastLock.mods[it.name] != null }
        return Pair(restored[true]!!, restored[false]!!)
    }

    private fun readLock(modsDir: File): Mods4LoadLock {
        val lockFile = modsDir.resolve(lockFile)
        return runCatching {
            val text = lockFile.readText(charset = Charsets.UTF_8)
            return Json.decodeFromString(text)
        }.getOrNull() ?: Mods4LoadLock()
    }

    private fun writeLock(modsDir: File, lock: Mods4LoadLock) {
        val lockFile = modsDir.resolve(lockFile)
        val json = Json.encodeToString(lock)
        lockFile.writeText(json, charset = Charsets.UTF_8)
    }
}

@Serializable
private data class Mod4LoadLock(
    val fileName: String,
)

@Serializable
private data class Mods4LoadLock(
    /** [Mod4LoadLock.fileName] to [Mod4LoadLock]*/
    val mods: Map<String, Mod4LoadLock> = mapOf(),
)