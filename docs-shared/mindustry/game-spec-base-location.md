**NOTE:** `Client` and `Server` configuration have almost the same syntax,
so in these chapters, `client/server` are used, which indicates that syntax can work both sides.
Please select the proper one.

### Game location

The game location is an abstract file, which represents the Mindustry game.

You can rename the downloaded file with this clause, `named`.

=== "Groovy"

    ``` groovy
    client/server {
        official version: latest named 'LatestClient.jar'
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry official latest named "LatestClient.jar"
    }
    ```

MGPP provides some simple clauses for downloading Mindustry:
- [official](#official)
- [be](#bleeding-edge)

#### Official

To download the game from [Mindustry official release](https://github.com/Anuken/Mindustry/releases).  

=== "Groovy"

    ``` groovy
    client/server {
        official version: 'v135'
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry official "v135"
    }
    ```

As default, mgpp will download `v135`.

##### Notations
- latest: To download the latest official Mindustry from [Mindustry official release](https://github.com/Anuken/Mindustry/releases), such as `v135.2`.

=== "Groovy"

    ``` groovy
    client/server {
        official version: latest
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry official latest
    }
    ```

#### Bleeding-edge

To download the bleeding-edge from [Mindustry bleeding-edge release](https://github.com/Anuken/MindustryBuilds/releases).  

=== "Groovy"

    ``` groovy
    client/server {
        be version: 'v135'
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry be "v135"
    }
    ```

As default, mgpp will download a certain version to prevent errors when gradle configuring,
but you shouldn't except this behavior.

##### Notations
- latest: To download the latest bleeding-edge Mindustry from [Mindustry bleeding-edge release](https://github.com/Anuken/MindustryBuilds/releases), such as `22826`.

=== "Groovy"

    ``` groovy
    client/server {
        be version: latest
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        be official latest
    }
    ```

#### From local file
To copy the game file from a local file.

=== "Groovy"

    ``` groovy
    client/server {
        fromLocal 'E:\Mindustry\myMindustry.jar'
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry fromLocal 'E:\Mindustry\myMindustry.jar'
    }
    ```

#### From local properties

To copy the game file with its path from `local.properties`,
you should declare the key-value pair in `local.properties` file first.

- For `client`: `mgpp.client.location`
- For `server`: `mgpp.server.location`

Then let mgpp find it from `local.properties` file with the `localProperties` notation.

=== "Groovy"

    ``` groovy
    client/server {
        from localProperties
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry from localProperties
    }
    ```
