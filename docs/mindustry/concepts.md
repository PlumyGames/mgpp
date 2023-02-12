# Concepts

### Notations

The notation always represents a term or works as a pronoun,
such as `latest`, `localProperties`, `latestRelease`.

### On The Fly

It indicates this task will be registered [after project evaluation](https://docs.gradle.org/current/userguide/build_lifecycle.html#sec:project_evaluation),
so you Failed to access it in your build.gradle[.kts] normally.

You have to access it in this way:
=== "Groovy"

    ``` groovy
    tasks.whenTaskAdded { it ->
        if (it.name == 'jar') {
            // do something
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    tasks.whenTaskAdded {
        if (name == "jar" && this is org.gradle.jvm.tasks.Jar) {
            // do something
        }
    }
    ```