pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://repo.legacyfabric.net/repository/legacyfabric/")
    }
}

includeBuild("../essential-gradle-toolkit")

rootProject.name = "ZRenderLib"
rootProject.buildFileName = "root.gradle.kts"

val versionList = listOf(
    "26.2-fabric",
    "26.1.2-fabric",
)
versionList.forEach { version ->
    file("versions/$version").mkdirs()
}

versionList.forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}

includeBuild("../ZKeys") {
    dependencySubstitution {
        versionList.forEach { version ->
            substitute(module("org.zephy.zkeys:$version")).using(project(":$version"))
        }
    }
}
