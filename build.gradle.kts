plugins {
    id("java")
    alias(libs.plugins.loom)
}

version = "0.8.0"
group = "net.cacaovisualclient.mod"

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.loader)
    modImplementation(libs.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.gson)

    implementation(libs.discord.ipc)
    include(libs.discord.ipc)

    implementation(libs.junixsocket.common)
    include(libs.junixsocket.common)

    implementation(libs.junixsocket.native.common)
    include(libs.junixsocket.native.common)

    implementation(libs.reflect)
    include(libs.reflect)
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
