import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.liplum.dsl.fromJson
import org.junit.jupiter.api.Test

data class Outer(
    val a: InnerA,
    val b: InnerB,
)

data class InnerA(
    val name: String,
    val list: List<String>,
)

data class InnerB(
    val bool: Boolean,
    val nested: Nested,
)

data class Nested(
    val number: Int,
)

val gson: Gson = GsonBuilder().apply {
    setPrettyPrinting()
}.create()

class TestJson {
    @Test
    fun `test nested object`() {
        val obj = Outer(
            a = InnerA(
                name = "Inner A",
                list = listOf("e1,e2,e3")
            ),
            b = InnerB(
                bool = false,
                nested = Nested(666)
            )
        )
        val json = gson.toJson(obj)
        val text = json.toString()
        println(text)
    }
    @Test
    fun `test restore nested object`() {
        val json = """
            {
              "a": {
                "name": "Restore A",
                "list": [
                  "a","b","c"
                ]
              },
              "b": {
                "bool": true,
                "nested": {
                  "number": 123
                }
              }
            }
        """.trimIndent()
        val obj = gson.fromJson<Outer>(json)
        assert(obj.a.name == "Restore A")
        assert(obj.a.list == listOf("a", "b", "c"))
        assert(obj.b.bool)
        assert(obj.b.nested == Nested(number = 123))
    }
}
