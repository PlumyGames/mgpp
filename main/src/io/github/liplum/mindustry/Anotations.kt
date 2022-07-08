package io.github.liplum.mindustry

/**
 * It indicates this property will inherit value from its parent project as default.
 */
@Target(
    AnnotationTarget.FIELD, AnnotationTarget.PROPERTY
)
annotation class InheritFromParent
@Target(
    AnnotationTarget.FIELD
)
/**
 * It represents the default value of this property.
 */
annotation class DefaultValue(
    val default: String,
)