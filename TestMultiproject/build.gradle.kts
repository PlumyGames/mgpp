import io.github.liplum.mindustry.mindustry

group = "plumy.test"
version = "1.0"
plugins {
    id("io.github.liplum.mgpp") version "2.0.0"
}
mindustry {
    dependency {
        mindustry on "v146"
        arc on "v146"
    }
}
runMindustry {
    addClient {
        official(version = "v146")
    }
    addServer {
        official(version = "v146")
    }
}