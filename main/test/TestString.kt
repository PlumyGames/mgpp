import io.github.liplum.dsl.linkString
import io.github.liplum.dsl.packageAndClassName
import org.junit.jupiter.api.Test

typealias QN = Pair<String, String>

class TestString {
    val empty = QN("", "")
    @Test
    fun `test split package and class name`() {
        "net.liplum.Clz" match ("net.liplum" o "Clz")
        "net.liplum" match ("net" o "liplum")
        "Clz" match ("" o "Clz")
        "a" match ("" o "a")
        "" match empty
        "." match empty
    }

    infix fun String.match(b: QN) =
        assert(this.packageAndClassName() == b)

    infix fun String.o(clz: String): QN =
        QN(this, clz)

    fun `test link string`() {
        "net-liplum" match listOf("net", "liplum")
        "net-liplum-mindustry" match listOf("net", "", "liplum", "", "mindustry")
    }

    infix fun String.match(strings: List<String>) =
        assert(this == linkString("-", strings))
}