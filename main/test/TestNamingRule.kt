import org.junit.jupiter.api.Test
import plumy.mindustry.NameRule

class TestNamingRule {
    @Test
    fun `test split`() {
        NameRule.Pascal.split("ImPascalNameRule") match listOf("im", "pascal", "name", "rule")
        NameRule.Camel.split("camelNameRule") match listOf("camel", "name", "rule")
        NameRule.Snake.split("here_is_a_python") match listOf("here", "is", "a", "python")
        NameRule.AllCaps.split("CPP_CONVENTION") match listOf("cpp", "convention")
        NameRule.Kebab.split("yummy-kebab") match listOf("yummy", "kebab")
    }
    @Test
    fun `test compose`() {
        val test = listOf("test", "the", "name", "rule")
        NameRule.Pascal.rename(test) match "TestTheNameRule"
        NameRule.Camel.rename(test) match "testTheNameRule"
        NameRule.Snake.rename(test) match "test_the_name_rule"
        NameRule.AllCaps.rename(test) match "TEST_THE_NAME_RULE"
        NameRule.Kebab.rename(test) match "test-the-name-rule"
    }

    infix fun List<String>.match(pattern: List<String>) {
        assert(this == pattern) { this.toString() }
    }

    infix fun String.match(equal: String) {
        assert(this == equal) { this }
    }
}