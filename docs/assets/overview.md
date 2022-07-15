# Mindustry Assets

The `mindustryAssets` extension allows you to configure assets, 
including `sprites`, `bundles`, `shaders`, `sounds` and anything
you want to add into your mod.

___

Assume your project has this structure:
```
YourMod/
├─ assets/
│  ├─ bundles/
│  ├─ sprites/
├─ src/
│  ├─ example/
│  │  ├─ ExampleMod.java
├─ build.gradle
├─ gradle.properties

```

### Simple usage

In most cases, you can just simply copy all files
recursively from the `assets root folder` into the `jar` task with this syntax:

=== "Groovy"

    ``` groovy
    mindustryAssets {
        rootAt "$projectDir/assets"
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    mindustryAssets {
        root at "$projectDir/assets"
    }
    ```

### Advanced usage

MGPP provides `AssetBatchType` and `AssetBatch` for generating resource class,
named `R.java` conventionally.