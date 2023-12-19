package io.github.liplum.mindustry.run.model

open class NamedModel(
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
    val name: String,
    /**
     * Whether this is anonymous.
     */
    val isAnonymous: Boolean,
)

