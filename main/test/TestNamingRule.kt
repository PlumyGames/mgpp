import io.github.liplum.mindustry.NameRule
import org.junit.jupiter.api.Test

class TestNamingRule {
    @Test
    fun `test split`() {
        NameRule.Pascal.split("ImPascalNameRule") match listOf("Im", "Pascal", "Name", "Rule")
        NameRule.Camel.split("camelNameRule") match listOf("camel", "Name", "Rule")
        NameRule.Snake.split("here_is_a_python") match listOf("here", "is", "a", "python")
        NameRule.AllCaps.split("CPP_CONVENTION") match listOf("cpp", "convention")
        NameRule.Kebab.split("yummy-kebab") match listOf("yummy", "kebab")
        NameRule.Domain.split("dot.net") match listOf("dot", "net")
    }
    @Test
    fun `test compose`() {
        val test = listOf("test", "the", "name", "rule")
        NameRule.Pascal.rename(test) match "TestTheNameRule"
        NameRule.Camel.rename(test) match "testTheNameRule"
        NameRule.Snake.rename(test) match "test_the_name_rule"
        NameRule.AllCaps.rename(test) match "TEST_THE_NAME_RULE"
        NameRule.Kebab.rename(test) match "test-the-name-rule"
        NameRule.Domain.rename(test) match "test.the.name.rule"
    }

    private
    infix fun List<String>.match(pattern: List<String>) {
        assert(this == pattern) { this.toString() }
    }

    private
    infix fun String.match(equal: String) {
        assert(this == equal) { this }
    }
    @Test
    fun `test match name rule`() {
        assert(NameRule.valueOf("pascal") == NameRule.Pascal)
        assert(NameRule.valueOf("caMEL") == NameRule.Camel)
        assert(NameRule.valueOf("snAke") == NameRule.Snake)
        assert(NameRule.valueOf("ALLCAPS") == NameRule.AllCaps)
        assert(NameRule.valueOf("kEbAb") == NameRule.Kebab)
        assert(NameRule.valueOf("DOMAIN") == NameRule.Domain)
        assert(NameRule.valueOf("?") == null)
    }
}