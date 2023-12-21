import io.github.liplum.mindustry.*
plugins {
    id("io.github.liplum.mgpp") version "2.0.0"
}
runMindustry {
    addClient {
        official(version = "v146")
    }
}