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
 * It indicates this property with [key] will be overwritten from `local.properties` file in the project's root directory
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
/**
 * It indicates this task won't be registered when [plugin] isn't applied.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY, AnnotationTarget.EXPRESSION, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION
)
@MustBeDocumented
annotation class DisableIfWithout(
    /**
     * When which plugin isn't applied, the task won't be registered.
     */
    val plugin: String,
)