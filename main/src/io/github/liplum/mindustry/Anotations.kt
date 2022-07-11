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
@Target(
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
@MustBeDocumented
annotation class DisableIfWithout(
    val plugin: String,
)