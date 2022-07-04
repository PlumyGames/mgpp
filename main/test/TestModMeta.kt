import io.github.liplum.dsl.toMap
import io.github.liplum.mindustry.ModMeta
import org.hjson.JsonObject
import org.junit.jupiter.api.Test

class TestModMeta {
    @Test
    fun `test mod meta +=`() {
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
    @Test
    fun `load from json`() {
        val json = """
            author: Liplum
            main: plumy.test.TestModGroovy
            version: 1.1
            minGameVersion: 136
            dependencies: [
                "cyber-io","dst"
            ]
        """.trimIndent()
        val jsonV = JsonObject.readHjson(json)
        println(jsonV)
        val obj = jsonV.toMap()
        println(obj)
    }
}