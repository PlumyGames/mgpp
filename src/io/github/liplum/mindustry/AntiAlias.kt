package io.github.liplum.mindustry

import arc.files.Fi
import arc.graphics.Color
import arc.graphics.Pixmap
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun Pixmap.getRGB(ix: Int, iy: Int): Int =
    getRaw(max(min(ix, width - 1), 0), max(min(iy, height - 1), 0))
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