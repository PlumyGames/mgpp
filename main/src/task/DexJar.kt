package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.AbstractOptions
import java.io.File

open class DexJar : DefaultTask() {
    val jarFiles = project.configurationFileCollection()
        @InputFiles @SkipWhenEmpty
        @PathSensitive(PathSensitivity.ABSOLUTE)
        get
    val classpath = project.configurationFileCollection()
        @InputFiles get
    val dexedJar = project.fileProp()
        @OutputFile get
    val options: DexJarOptions = new()
        @Input @Optional get

    init {
        dexedJar.convention(temporaryDir.resolve("dexed.jar"))
    }
    @TaskAction
    fun dex() {
        val dexedJarFile = dexedJar.get()
        dexedJarFile.parentFile.mkdirs()
        val dexedJarPath = dexedJarFile.absolutePath
        val jars = jarFiles.files
        // Check space in absolute path
        val jarToDexPaths = jars.map { it.path }.toList()
        // TODO: Escape the space instead of exception
        if (" " in dexedJarPath) throw GradleException("d8 doesn't allow a path with any space but the dexed jar's path is \"$dexedJarFile\" .")
        for (jarPath in jarToDexPaths) {
            if (" " in jarPath) throw GradleException("d8 doesn't allow a path with any space but the path of a jar to be dexed is \"$jarPath\" .")
        }
        val sdkRoot = System.getenv("ANDROID_HOME")
            ?: System.getenv("ANDROID_SDK_ROOT")
            ?: throw GradleException("Android SDK not found. Ensure ANDROID_HOME or ANDROID_SDK_ROOT is in your environment.")
        val sdkRootDir = File(sdkRoot)
        if (!sdkRootDir.isDirectory) throw GradleException("Android SDK not found. Ensure ANDROID_HOME or ANDROID_SDK_ROOT is set to a valid directory.")
        val androidJarFile = run {
            // searching for the `android.jar` in Android SDK Path
            (sdkRootDir.resolve("platforms").listFiles() ?: emptyArray()).sorted().reversed()
                .platformFindAndroidJar()
                ?: throw GradleException("No android.jar found. Ensure that you have an Android platform installed.")
        }
        var d8 = "d8"
        // Check the default d8 command
        try {
            project.exec {
                it.commandLine = listOf(d8, "--help")
            }
        } catch (_: Exception) {
            logger.info("d8 isn't available on your platform, the absolute path of d8 will be found and utilized.")
            val d8File = run {
                // searching for the `android.jar` in Android SDK Path
                (sdkRootDir.resolve("build-tools").listFiles() ?: emptyArray()).sorted().reversed()
                    .platformFindD8()
                    ?: throw GradleException("d8 not found. Ensure that you have an Android build-tools installed.")
            }
            d8 = d8File.absolutePath
        }
        val classpaths = classpath.files + androidJarFile
        val params = ArrayList<String>(classpaths.size * 2 + jars.size + 5)
        params.add(d8)
        for (classpath in classpaths) {
            params.add("--classpath")
            params.add(classpath.path)
        }
        params.add("--min-api")
        params.add(options.minApi)
        params.add("--output")
        // Don't add quotes here, it doesn't work on linux
        params.add(dexedJarPath)
        params.addAll(jarToDexPaths)
        project.exec {
            it.commandLine = params
            it.standardOutput = System.out
            it.errorOutput = System.err
        }
    }
    //For Kotlin
    inline fun options(config: DexJarOptions.() -> Unit) {
        options.config()
    }
    // For Groovy
    fun options(config: Action<DexJarOptions>) {
        config.execute(options)
    }
}

open class DexJarOptions : AbstractOptions() {
    /**
     * --min-api 14 as default
     */
    var minApi = "14"
}

fun List<File>.platformFindD8(): File? =
    when (getOs()) {
        OS.Windows -> find { File(it, "d8.bat").exists() }?.run { File(this, "d8.bat") }
        OS.Linux -> find { File(it, "d8").exists() }?.run { File(this, "d8") }
        OS.Mac -> find { File(it, "d8").exists() }?.run { File(this, "d8") }
        else -> null
    }

fun List<File>.platformFindAndroidJar(): File? =
    find { File(it, "android.jar").exists() }?.run { File(this, "android.jar") }