import org.junit.jupiter.api.Test
import plumy.mindustry.ModMeta

class TestModMeta {
    @Test
    fun `test mod meta +=`(){
        val a = ModMeta(
            name = "Test Name",
            main = "net.liplum.MainClz"
        )
        println(a)
        a += ModMeta(
            name = "Overwritten name",
            version = "111111.0"
        )
        println(a)
    }
}