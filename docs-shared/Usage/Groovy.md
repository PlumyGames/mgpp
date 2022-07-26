
# Groovy Usage

<br>

***Please always make sure you are*** <br>
***on the latest version of MGPP***

<br>
<br>

## DSL Plugin

*If you want to use it as a **[DSL Plugin]**.*

<br>

```groovy
plugins {
    id "io.github.liplum.mgpp" version "<version>"
}
```

<br>
<br>

## Legacy Plugin

*If you want to use it as a **[Legacy Plugin]**.*

<br>

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "io.github.liplum.mgpp:MindustryGradlePluginPlumy:<version>"
    }
}

apply plugin: "io.github.liplum.mgpp"
```

<br>


<!----------------------------------------------------------------------------->

[Legacy Plugin]: https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application
[DSL Plugin]: https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block

