import io.github.liplum.dsl.toMap
import io.github.liplum.mindustry.*
import org.hjson.JsonObject
import org.junit.jupiter.api.Test

class TestModMeta {
    @Test
    fun `test mod meta +=`() {
        val a = ModMeta(
            name = "Test Name",
            main = "net.liplum.MainClz"
        )
        assert(a.name == "Test Name")
        assert(a.main == "net.liplum.MainClz")
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