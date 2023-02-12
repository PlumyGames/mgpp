package io.github.liplum.mindustry

import arc.files.Fi
import arc.graphics.Color
import arc.graphics.Pixmap
import io.github.liplum.dsl.dirProp
import io.github.liplum.dsl.new
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileType
import org.gradle.api.tasks.*
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.compile.AbstractOptions
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class AntiAlias : DefaultTask() {
    val sourceDirectory = project.dirProp()
        @Incremental @InputDirectory get
    val sourceFiles: ConfigurableFileCollection = project.files()
        @Incremental @InputFiles get
    val destinationDirectory = project.dirProp()
        @OutputDirectory get
    val filters = FileFilter.Set()
        @Internal get
    val options: AntiAliasingOptions = new()
        @Input @Optional get

    init {
        group = R.taskGroup.mindustry
    }
    @TaskAction
    fun process(inputs: InputChanges) {
        if (options.isIncremental && inputs.isIncremental) {
            preformIncrementalAA(inputs)
        } else {
            performFullAA()
        }
    }

    protected fun performFullAA() {
        val dest = destinationDirectory.asFile.get()
        logger.info("Setup full anti-alias: ${dest.absolutePath} will be deleted before.")
        dest.deleteRecursively()
        dest.mkdirs()
        logger.info("Full anti-alias in ${sourceDirectory.asFile.get().absolutePath}")
        sourceDirectory.asFileTree.toList().performAA()
    }

    protected fun preformIncrementalAA(inputs: InputChanges) {
        logger.info("Anti-aliasing incrementally in ${sourceDirectory.get().asFile.absolutePath} .")
        inputs.getFileChanges(sourceDirectory.asFileTree).mapNotNull {
            if (it.fileType == FileType.DIRECTORY) return@mapNotNull null
            val changedInDest = sourceDirectory.file(it.normalizedPath).get().asFile
            if (it.changeType == ChangeType.REMOVED) {
                logger.info("${it.file.absolutePath} will be deleted due to the removal of source.")
                changedInDest.delete()
                null
            } else {
                it.file
            }
        }.toList().performAA()
    }
    /**
     * @receiver an iterable of all source textures to be anti-aliased
     */
    protected fun Collection<File>.performAA() {
        val sourceRoot = sourceDirectory.asFile.get()
        val destDir = destinationDirectory.asFile.get()
        this.stream().parallel().forEach {
            if (!it.extension.equals("png", ignoreCase = true)) {
                logger.info("$it is skipped.")
                return@forEach
            }
            val relative = it.normalize().relativeTo(sourceRoot)
            val to = destDir.resolve(relative)
            to.parentFile.mkdirs()
            if (!filters.isAccept(it)) {
                if (to.exists()) {
                    to.delete()
                    logger.info("Deleted an ignored file ${to.absolutePath}.")
                }
                return@forEach
            }
            logger.info("[AntiAlias]${it.absolutePath} -> ${to.absolutePath}")
            try {
                antiAliasing(it, to)
            } catch (e: Exception) {
                logger.info("Failed to anti-alias ${it.absolutePath}", e)
            }
        }
    }

    fun clearFilter() {
        filters.clear()
    }

    fun removeFilter(filter: FileFilter) {
        filters -= filter
    }

    fun addFilter(filter: FileFilter) {
        filters += filter
    }
    /**
     * ### For Kotlin
     */
    inline fun options(config: AntiAliasingOptions.() -> Unit) {
        options.config()
    }
    /**
     * ### For Groovy
     */
    fun options(config: Action<AntiAliasingOptions>) {
        config.execute(options)
    }
}

open class AntiAliasingOptions : AbstractOptions() {
    var isIncremental = true
}

private fun Pixmap.getRGB(ix: Int, iy: Int): Int =
    getRaw(max(min(ix, width - 1), 0), max(min(iy, height - 1), 0))
/**
 * This algorithm comes from [Mindustry tools](https://github.com/Anuken/Mindustry/blob/master/tools/build.gradle#L31).
 */
@Suppress("LocalVariableName")
fun antiAliasing(from: File, to: File) {
    val image = Pixmap(Fi(from))
    val out = image.copy()
    val color = Color()
    val sum = Color()
    val suma = Color()
    val p = IntArray(9)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val A: Int = image.getRGB(x - 1, y + 1)
            val B: Int = image.getRGB(x, y + 1)
            val C: Int = image.getRGB(x + 1, y + 1)
            val D: Int = image.getRGB(x - 1, y)
            val E: Int = image.getRGB(x, y)
            val F: Int = image.getRGB(x + 1, y)
            val G: Int = image.getRGB(x - 1, y - 1)
            val H: Int = image.getRGB(x, y - 1)
            val I: Int = image.getRGB(x + 1, y - 1)
            Arrays.fill(p, E)
            if (D == B && D != H && B != F) p[0] = D
            if ((D == B && D != H && B != F && E != C) || (B == F && B != D && F != H && E != A)) p[1] = B
            if (B == F && B != D && F != H) p[2] = F
            if ((H == D && H != F && D != B && E != A) || (D == B && D != H && B != F && E != G)) p[3] = D
            if ((B == F && B != D && F != H && E != I) || (F == H && F != B && H != D && E != C)) p[5] = F
            if (H == D && H != F && D != B) p[6] = D
            if ((F == H && F != B && H != D && E != G) || (H == D && H != F && D != B && E != I)) p[7] = H
            if (F == H && F != B && H != D) p[8] = F
            suma.set(0)

            for (c in p) {
                color.rgba8888(c)
                color.premultiplyAlpha()
                suma.r(suma.r + color.r)
                suma.g(suma.g + color.g)
                suma.b(suma.b + color.b)
                suma.a(suma.a + color.a)
            }
            var fm = if (suma.a <= 0.001f) 0f else (1f / suma.a)
            suma.mul(fm, fm, fm, fm)
            var total = 0f
            sum.set(0)

            for (c in p) {
                color.rgba8888(c)
                val a = color.a
                color.lerp(suma, (1f - a))
                sum.r(sum.r + color.r)
                sum.g(sum.g + color.g)
                sum.b(sum.b + color.b)
                sum.a(sum.a + a)
                total += 1f
            }
            fm = 1f / total
            sum.mul(fm, fm, fm, fm)
            out.setRaw(x, y, sum.rgba8888())
            sum.set(0)
        }
    }
    image.dispose()
    out.dispose()

    Fi(to).writePng(out)
}