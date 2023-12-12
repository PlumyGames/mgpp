import io.github.liplum.mindustry.mindustry

group = "plumy.test"
version = "1.0"
plugins {
    id("io.github.liplum.mgpp") version "1.3.1"
}

mindustry {
    dependency {
        mindustry on "v146"
        arc on "v146"
    }
    client {
        mindustry official "v146"
    }
    server {
        mindustry official "v146"
    }
}
