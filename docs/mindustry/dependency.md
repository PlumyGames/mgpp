# Dependency

### Import

To apply the dependency configuration,
you have to call `importMindustry()` when [declaring dependency](https://docs.gradle.org/current/userguide/declaring_dependencies.html)
inside `dependencies{}` closure in your `build.gradle[.kts]`.

=== "Groovy"

    ``` groovy
    dependencies {
        importMindustry()
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    dependencies {
        importMindustry()
    }
    ```
MGPP provides `dependency{}` closure for you to configure which version you want to use,
and it's inside of [`mindustry{}`](overview.md) closure hereinafter.

___

### Arc dependency

You can specify the which Arc version to use with this syntax.

=== "Groovy"

    ``` groovy
    dependency {
        arc version: 'v135' // a released version
        // OR
        arc version: 'b1b88883e2' // a commit snapshot
    }
    ```

=== "Kotlin"

    ``` kotlin
    dependency {
        arc on "v135" // a released version
        // OR
        arc on "b1b88883e2" // a commit snapshot
    }
    ```

As default, mgpp will apply `arc v135`.

##### Notations

- latest: To apply the [latest commit](https://github.com/Anuken/Arc/commits/master) of Arc,
  such as `b1b88883e2`.
> **NOTE:** It might not work if you faced the API limit of GitHub or jitpack yet to build this version.
- latestTag: To apply the [latest tag](https://github.com/Anuken/Arc/tags) of Arc, such as `v135.2`.
> **NOTE:** It has a very small chance that it won't work when the new version was just released.

=== "Groovy"

    ``` groovy
    dependency {
        arc version: latest
        arc version: latestTag
    }
    ```

=== "Kotlin"

    ``` kotlin
    dependency {
        arc on latest
        arc on latestTag
    }
    ```

### Mindustry Dependency

You can specify the which Mindustry version to use with this syntax.

- [mindustry](https://jitpack.io/#anuken/mindustry): only supports released versions
- [mirror](https://jitpack.io/#anuken/mindustryJitpack): only supports commit snapshots

=== "Groovy"

    ``` groovy
    dependency {
        mindustry version: 'v135' // a released version of Mindustry
        // OR
        mindustryMirror version: '66595eb140' // a commit snapshot from mirror
    }
    ```

=== "Kotlin"

    ``` kotlin
    dependency {
        mindustry on "v135" // a released version of Mindustry
        // OR
        mindustry mirror "66595eb140" // a commit snapshot from mirror
    }
    ```

As default, mgpp will apply `mindustry v135`.

##### Notations

- latest
    - For `mindustry`: To apply the [latest release](https://github.com/Anuken/Mindustry/releases) of Mindustry,
      such as `v135.2`.
> **NOTE:** It has a very small chance that it won't work when the new version was just released.

    - For `mirror`: To apply the [latest commit](https://github.com/Anuken/MindustryJitpack/commits/main) of Mindustry mirror,
      such as `66595eb140`.
> **NOTE:** It might not work if you faced the API limit of GitHub or jitpack yet to build this version.

- latestRelease:
    - Only for `mindustry`: To apply the [latest release](https://github.com/Anuken/Mindustry/releases) of Mindustry,
      completely the same as `mindustry latest`, such as `v135.2`.
> **NOTE:** It has a very small chance that it won't work when the new version was just released.

=== "Groovy"

      ``` groovy
      dependency {
          mindustry version: latest
          mindustry version: latestRelease
          mindustryMirror version: latest
      }
      ```

=== "Kotlin"

    ``` kotlin
    dependency {
        mindustry on latest
        mindustry on latestRelease
        mindustry mirror latest
    }
    ```
