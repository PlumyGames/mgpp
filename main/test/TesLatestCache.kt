import com.google.gson.GsonBuilder
import io.github.liplum.dsl.fromJson
import io.github.liplum.mindustry.LatestCache
import org.junit.jupiter.api.Test

class TesLatestCache {
    @Test
    fun `test json serialization and deserialization`() {
        val cache = LatestCache(
            name = "devil",
            lastValue = "666",
            lastUpdatedTimeStamp = System.currentTimeMillis()
        )
        val gson = GsonBuilder().apply {
            setPrettyPrinting()
        }.create()
        val json = gson.toJson(cache)
        println(json)
        val restored = gson.fromJson<LatestCache>(json)
        assert(cache.name == restored.name)
        assert(cache.lastValue == restored.lastValue)
        assert(cache.lastUpdatedTimeStamp == restored.lastUpdatedTimeStamp)
    }
}