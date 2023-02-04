package io.github.liplum.mindustry

import org.gradle.api.Project

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
 * It indicates this property corresponds to a key in `local.properties` file.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
annotation class LocalProperty(
    /**
     * The key in `local.properties` file
     */
    val key: String,
)
/**
 * It indicates this property will read default value from [Project.getProperties]
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
annotation class PropertyAsDefault(
    /**
     * The key in [Project.getProperties]
     */
    val key: String,
    /**
     * If the key doesn't exist, this value will be truly default.
     */
    val default: String,
)
