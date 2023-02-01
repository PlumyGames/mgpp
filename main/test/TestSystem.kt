import io.github.liplum.dsl.getOs
import org.junit.jupiter.api.Test

class TestSystem {
    /**
     * It's dangerous as a normal test when running on the CI.
     */
    fun `test output system properties`() {
        System.getProperties().entries.sortedBy {
            it.key.toString()
        }.forEach {
            var value = it.value.toString()
            value = if (value.length > 103) value.substring(0, 100) + "..." else value
            println("${it.key}=${value}")
        }
    }
    @Test
    fun `test check os`() {
        println("Operation system: ${getOs()}")
    }
}
