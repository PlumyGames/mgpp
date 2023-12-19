import io.github.liplum.dsl.getDuplicateName
import org.junit.jupiter.api.Test

class TestDuplicateName {
    @Test
    fun `test no trailing number`() {
        "modpack" expectConvertTo "modpack 2"
    }
    @Test
    fun `test trailing number`() {
        "modpack 5" expectConvertTo "modpack 6"
    }

    @Test
    fun `test trailing number with long spacing`() {
        "modpack    1  5" expectConvertTo "modpack    1  6"
    }

    private
    infix fun String.expectConvertTo(excepted: String) {
        val new = this.getDuplicateName()
        assert(new == excepted){
            print("$new is not equal to $excepted")
        }
    }
}