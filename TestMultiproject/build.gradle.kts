import io.github.liplum.mindustry.mindustry

group = "plumy.test"
version = "1.0"
plugins {
    id("io.github.liplum.mgpp") version "1.0.13"
}
mindustry {
    dependency {
        mindustry mirror "d7312445a1"
        arc on "123fbf12b9"
    }
}