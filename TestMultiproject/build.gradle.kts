import io.github.liplum.mindustry.mindustry

group = "plumy.test"
version = "1.0"
plugins {
    id("io.github.liplum.mgpp") version "1.1.9"
}
mindustry {
    dependency {
        mindustry on "v136"
        arc on "v136"
    }
    client {
        mindustry official "v135"
    }
    server {
        mindustry official "v135"
    }
}