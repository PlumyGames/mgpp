package io.github.liplum.mindustry

/**
 * It indicates this property will inherit value from its parent project by default.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
@MustBeDocumented
annotation class InheritFromParent
