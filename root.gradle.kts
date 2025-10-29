plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    val forge10809 = createNode("1.8.9-forge", 10809, "mcp")
    val forge11202 = createNode("1.12.2-forge", 11202, "intermediary")
    val fabric11202 = createNode("1.12.2-fabric", 11202, "intermediary")
    val fabric11404 = createNode("1.14.4-fabric", 11404, "intermediary")
    val fabric11602 = createNode("1.16.2-fabric", 11602, "intermediary")

    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")
    val fabric12106 = createNode("1.21.6-fabric", 12106, "yarn")
    val fabric12107 = createNode("1.21.7-fabric", 12107, "yarn")
    val fabric12108 = createNode("1.21.8-fabric", 12108, "yarn")
    val fabric12109 = createNode("1.21.9-fabric", 12109, "yarn")
    val fabric12110 = createNode("1.21.10-fabric", 12110, "yarn")
    val fabric12111 = createNode("1.21.11-fabric", 12111, "yarn")

    forge10809.link(forge11202)
    forge11202.link(fabric11202)
    fabric11202.link(fabric11404)
    fabric11404.link(fabric11602)
    fabric11602.link(fabric12105)

    fabric12105.link(fabric12106)
    fabric12106.link(fabric12107)
    fabric12107.link(fabric12108)
    fabric12108.link(fabric12109)
    fabric12109.link(fabric12110)
    fabric12110.link(fabric12111)
}
