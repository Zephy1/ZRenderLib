plugins {
    kotlin("jvm")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

group = "org.zephy.zrenderlib"
version = "1.0.0"

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
configurations.getByName("implementation").extendsFrom(embed)

dependencies {
    if (project.platform.mcVersion < 12100 && project.platform.mcVersion != 10809) return@dependencies

    if (project.platform.mcVersion <= 12100) {
        modCompileOnly("gg.essential:essential-$platform:4167+g4594ad6e6")
        embed("gg.essential:loader-launchwrapper:1.2.3")
    }

    modCompileOnly("org.zephy.zkeys:${project.platform}:1.0.0") { isTransitive = false }
}

tasks {
    shadowJar {
        configurations.set(listOf(embed))
        exclude("gg/essential/**")
    }
    withType<net.fabricmc.loom.task.RemapJarTask>().configureEach {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}

preprocess {
    vars.put("FABRIC", if (project.platform.isFabric) 1 else 0)
    vars.put("!FABRIC", if (project.platform.isFabric) 0 else 1)
}

afterEvaluate {
    val hasRemapJar = tasks.findByName("remapJar") != null
    val outputTaskName = if (hasRemapJar) "remapJar" else "shadowJar"

    tasks.register<Copy>("collectJars") {
        group = "build"
        description = "Copies this version's non-shadowed JARs to main/jars"

        val outputDir = projectDir.resolve("../../jars").normalize()
        dependsOn(outputTaskName)

        from(tasks.named(outputTaskName)) {
            include("*.jar")
            exclude { it.name.contains(" 1.2") && it.name.contains("-all") }
            exclude { it.name.contains(" 1.1") }
            rename { fileName ->
                fileName
                    .replace(".jar", ".unloaded")
                    .replace("-forge", "")
                    .replace("-fabric", "")
                    .replace("-all", "")
                    .replace(" ", "-")
                    .replace("-$version", "")
            }
        }
        into(outputDir)
    }

    tasks.named("build") {
        finalizedBy("collectJars")
    }

    configurations.named("default") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }

    artifacts {
        add("default", tasks.named(outputTaskName))
    }
}
