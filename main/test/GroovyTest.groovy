import org.junit.jupiter.api.Test
import plumy.mindustry.MindustryExtension

class GroovyTest {
    def test() {
        new MindustryExtension(null).with {
            dependency.with {
                mindustry.mirror ""
            }
        }
    }
}
