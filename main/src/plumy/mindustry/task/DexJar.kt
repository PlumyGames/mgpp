package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.*
import plumy.dsl.*
import java.io.ByteArrayOutputStream
import java.io.File

open class DexJar : DefaultTask() {
    val jarFiles = project.configurationFileCollection()
        @InputFiles get
    val classpath = project.configurationFileCollection()
        @InputFiles get
    val androidJar = project.fileProp()
        @InputFile @Optional get
    val sdkRoot = project.stringProp()
        @Input @Optional get
    val workingDir = project.fileProp()
        @Optional @Input get
    /**
     * The path or command of d8.
     * `d8` as the convention.
     */
    val d8 = project.stringProp()
        @Optional @Input get
    val dexedJar = project.fileProp()
        @Optional @OutputFile get

    init {
        d8.convention("d8")
        dexedJar.convention(temporaryDir.resolve("dexed.jar"))
        workingDir.convention(temporaryDir)
        sdkRoot.convention(System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: "")
    }
    @TaskAction
    fun dex() {
        // If the android isn't specified, try to find it
        val androidJarFile = if (androidJar.isPresent)
            androidJar.get()
        else {
            // searching for the `android.jar` in Android SDK Path
            val sdkPath = sdkRoot.get()
            if (sdkPath.isBlank()) throw GradleException("No valid Android SDK found. Ensure that ANDROID_HOME is set to your Android SDK directory.")
            val platformRoot = File("$sdkPath/platforms/").listFiles()!!.sorted().reversed()
                .find { f -> File(f, "android.jar").exists() }
                ?: throw GradleException("No android.jar found. Ensure that you have an Android platform installed.")
            File(platformRoot, "android.jar")
        }
        // try check d8
        var d8 = d8.get()
        try {
            project.exec {
                it.commandLine = listOf(d8)
            }
        } catch (_: Exception) {
            logger.info("d8 not found, now try to find it.")
            d8 = project.tryFindD8() ?: throw GradleException("Can't execute or even find d8.")
        }
        val classpaths = classpath.files + androidJarFile
        val jars = jarFiles.files
        val params = ArrayList<String>(classpaths.size * 2 + jars.size + 4)
        params.add(d8)
        for (classpath in classpaths) {
            params.add("--classpath")
            params.add(classpath.path)
        }
        params.add("--min-api")
        params.add("14")
        params.add("--output")
        params.add("\"${dexedJar.get().absolutePath}\"")
        params.addAll(jars.map { "\"${it.absolutePath}\"" })
        project.exec {
            it.commandLine = params
            it.workingDir = workingDir.get()
            it.standardOutput = System.out
            it.errorOutput = System.err
        }
    }
}

fun Project.tryFindD8(): String? {
    return runCatching {
        when (getOs()) {
            OS.Unknown -> {
                logger.warn("Can't recognize your operation system.")
                null
            }
            OS.Windows, OS.Linux -> {
                val cmdOutput = ByteArrayOutputStream()
                exec {
                    it.commandLine = listOf("where", "d8")
                    it.standardOutput = cmdOutput
                }
                cmdOutput.toString().replace("\r", "").replace("\n", "").apply {
                    logger.info("d8 found at $this")
                }
            }
            OS.Mac -> {
                val cmdOutput = ByteArrayOutputStream()
                exec {
                    it.commandLine = listOf("which", "d8")
                    it.standardOutput = cmdOutput
                }
                cmdOutput.toString().replace("\r", "").replace("\n", "").apply {
                    logger.info("d8 found at $this")
                }
            }
        }
    }.getOrNull()
}
