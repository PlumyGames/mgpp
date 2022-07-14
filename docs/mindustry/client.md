# Client
The `client{}` closure is inside of [`mindustry{}`](overview.md) hereinafter.

--8<--
docs-shared/mindustry/game-spec-base-location.md
--8<--

#### Foo's Client
To download the Foo's Client from [AntiGrief release](https://github.com/mindustry-antigrief/mindustry-client/releases).

**NOTE:** Since Foo's Client isn't an official client, mgpp
doesn't guarantee the arguments below will properly work in your times.

At lease, as of press date, it works. 
Therefore, you may have to configure it on your own if necessary. 

=== "Groovy"

    ``` groovy
    client/server {
        from Foo(
            version: 'v8.0.0',
            release: 'erekir-client.jar'
        )
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        mindustry from Foo(
            version = "v8.0.0",
            release = "erekir-client.jar"
        )
    }
    ```

- version: The tag/version of which release you want to download.
- release: The name of file attached into the release.


--8<--
docs-shared/mindustry/game-spec-base-secondary.md
--8<--