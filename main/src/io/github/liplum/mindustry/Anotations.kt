package io.github.liplum.mindustry

/**
 * It indicates this property will inherit value from its parent project as default.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
@MustBeDocumented
annotation class InheritFromParent
/**
 * It represents the default value of this property.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
@MustBeDocumented
annotation class DefaultValue(
    val default: String,
)
/**
 * It indicates this property will be overwritten from `local.properties` with [key]
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
annotation class LocalProperty(
    val key: String,
)
/**
 * It indicates this task won't be registered when [plugin] isn't applied.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY, AnnotationTarget.EXPRESSION
)
@MustBeDocumented
annotation class DisableIfWithout(
    val plugin: String,
)