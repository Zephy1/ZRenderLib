plugins {
    kotlin("jvm")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

group = "org.zephy.zrenderlib"

loom {
    runConfigs {
        named("client") {
            ideConfigGenerated(true)
            programArgs("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
        }
    }
}

configurations.all {
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-json-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-core-jvm")
}

val embed by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    if (project.platform.mcVersion < 12100 && project.platform.mcVersion != 10809) return@dependencies

    if (project.platform.mcVersion <= 12100) {
        modCompileOnly("gg.essential:essential-$platform:4167+g4594ad6e6")
        embed("gg.essential:loader-launchwrapper:1.2.3")
    } else {
        when (project.platform.mcVersion) {
            12105 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.5-fabric:436")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.128.2+1.21.5")
            }
            12106 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.6-fabric:436")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.128.2+1.21.6")
            }
            12107 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.7-fabric:436")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.129.0+1.21.7")
            }
            12108 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.7-fabric:436")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.136.0+1.21.8")
            }
            12109 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.9-fabric:436")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.134.0+1.21.9")
            }
            12110 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.9-fabric:436")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.136.0+1.21.10")
            }
            12111 -> {
                modCompileOnly("gg.essential:universalcraft-1.21.11-fabric:446")
                modImplementation("net.fabricmc.fabric-api:fabric-api:0.140.2+1.21.11") {
                    exclude(group = "net.fabricmc.fabric-api", module = "fabric-content-registries-v0")
                }
            }
            else -> throw IllegalStateException("Unsupported MC version: ${project.platform.mcVersion}")
        }
        modImplementation("net.fabricmc:fabric-loader:0.18.4")
        modImplementation("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
    }
}

tasks {
    shadowJar {
        configurations = listOf(embed)
        exclude("gg/essential/**")
    }

    remapJar {
        input.set(shadowJar.get().archiveFile)
    }
}

tasks.register<Copy>("collectJars") {
    group = "build"
    description = "Copies this version’s non-shadowed JARs to main/jars"

    val outputDir = projectDir.resolve("../../jars").normalize()
    dependsOn("remapJar")

    from(tasks.named("remapJar")) {
        include("*.jar")
        exclude("*-all.jar")

        exclude { fileTreeElement ->
            fileTreeElement.name.contains(" 1.1")
        }

        rename { fileName ->
            fileName
                .replace(".jar", ".unloaded")
                .replace("-forge", "")
                .replace("-fabric", "")
                .replace(" ", "-")
        }
    }
    into(outputDir)
}
tasks.named("build") {
    finalizedBy("collectJars")
}

preprocess {
    vars.put("FABRIC", if (project.platform.isFabric) 1 else 0)
    vars.put("!FABRIC", if (project.platform.isFabric) 0 else 1)
}
