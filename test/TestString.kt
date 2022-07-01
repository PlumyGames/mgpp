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
        this.packageAndClassName() == b

    infix fun String.o(clz: String): QN =
        QN(this, clz)
}