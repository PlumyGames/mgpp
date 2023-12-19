package io.github.liplum.mindustry

import io.github.liplum.dsl.getDuplicateName
import io.github.liplum.mindustry.formatValidGradleName
import java.io.Serializable

interface NamedModel : Serializable {
    /**
     * *Optional*
     * An empty String as default.
     * It affects gradle task names.
     * ```
     * runClient // if it's anonymous
     * runClient2 // if second name is still anonymous
     * runClientFooClient // if [name] is "FooClient"
     * runServer // if it's anonymous
     * ```
     */
    val name: String

    /**
     * Whether this is anonymous.
     */
    val isAnonymous: Boolean
}

/**
 * Allocate a model name without name collision.
 * @return (newName, isAnonymous)
 */
fun <T : NamedModel> allocModelName(name: String, all: List<T>): Pair<String, Boolean> {
    var newName = formatValidGradleName(name)
    val isAnonymous = newName.isBlank()
    if (isAnonymous) {
        val anonymousCount = all.count { it.isAnonymous }
        newName = if (anonymousCount == 0) ""
        else (anonymousCount + 1).toString()
    } else if (all.any { it.name == newName }) {
        newName = formatValidGradleName(name.getDuplicateName())
    }
    return Pair(newName, isAnonymous)
}