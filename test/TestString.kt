import io.github.liplum.dsl.packageAndClassName
import org.junit.jupiter.api.Test

class TestString {
    @Test
    fun `test split package and class name`(){
        println("net.liplum.Clz".packageAndClassName())
        println("net.liplum".packageAndClassName())
        println("Clz".packageAndClassName())
        println("a".packageAndClassName())
        println("".packageAndClassName())
        println(".".packageAndClassName())
    }
}