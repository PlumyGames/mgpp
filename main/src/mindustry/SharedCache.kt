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

    fun resolveCacheDir(): File {
        return resolveGradleUserHome().resolve("mindustry-mgpp")
    }

    fun cleanCache() {
        val dir = resolveCacheDir()
        if (dir.isDirectory) {
            dir.deleteRecursively()
        }
    }
}