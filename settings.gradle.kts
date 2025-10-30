pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://repo.legacyfabric.net/repository/legacyfabric/")
    }

    plugins {
        id("com.github.johnrengelman.shadow") version "8.1.1"
    }
}

// !! This uses my own fork of the toolkit, I couldn't get 1.21.9+ to build on the maven build (I couldn't update past Loom 1.9.x due to depreciated methods) !!
includeBuild("../essential-gradle-toolkit")
rootProject.name = "ZRenderLib"
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.8.9-forge",
    "1.12.2-forge",
    "1.12.2-fabric",
    "1.14.4-fabric",
    "1.16.2-fabric",
    "1.21.5-fabric",
    "1.21.8-fabric",
    "1.21.10-fabric",
//    "1.21.11-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
