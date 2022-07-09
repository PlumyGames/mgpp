package io.github.liplum.mindustry

import io.github.liplum.dsl.ensure
import io.github.liplum.dsl.fromJson
import io.github.liplum.dsl.gson
import org.gradle.api.Project
import kotlin.math.absoluteValue

data class LatestCache(
    var name: String,
    var lastValue: String,
    var lastUpdatedTimeStamp: Long,
)

inline fun Project.getLatestVersion(
    type: String,
    outOfDate: Long = Mgpp.outOfDataTime,
    fetch: () -> String,
): String {
    val mindustry = rootProject.buildDir.resolve("mindustry")
    val jsonFile = mindustry.resolve("latest_cache.json").ensure()
    val json = if (jsonFile.exists())
        jsonFile.readText()
    else "[]"
    val all = runCatching {
        gson.fromJson<Array<LatestCache>>(json).toMutableList()
    }.getOrDefault(ArrayList())
    val cache = all.find { it.name == type }
    val res: String
    var changed = false
    if (cache != null) {
        val curTime = System.currentTimeMillis()
        if (curTime - cache.lastUpdatedTimeStamp < outOfDate.absoluteValue) {
            res = cache.lastValue
        } else { // out of date
            res = fetch()
            cache.lastUpdatedTimeStamp = System.currentTimeMillis()
            changed = true
        }
    } else {
        res = fetch()
        all.add(
            LatestCache(
                name = type,
                lastValue = res,
                lastUpdatedTimeStamp = System.currentTimeMillis()
            )
        )
        changed = true
    }
    if (changed) {
        all.sortBy { it.name }
        jsonFile.writeText(gson.toJson(all))
    }
    return res
}
