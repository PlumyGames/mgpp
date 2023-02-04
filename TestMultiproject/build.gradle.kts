import io.github.liplum.mindustry.mindustry

group = "plumy.test"
version = "1.0"
plugins {
    id("io.github.liplum.mgpp") version "2.0.0"
}
mindustry {
    dependency {
        mindustry on "v141.3"
        arc on "v141.3"
    }
}
runMindustry {
    addClient {
        official(version = "v141.3")
    }
    addServer {
        official(version = "v141.3")
    }
}