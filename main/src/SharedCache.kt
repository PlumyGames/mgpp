package io.github.liplum.mindustry

import java.io.File

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

    fun cleanCache() {
        val dir = cacheDir
        if (dir.isDirectory) {
            dir.deleteRecursively()
        }
    }
}