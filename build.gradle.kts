plugins {
    id("java")
    alias(libs.plugins.loom)
}

version = "0.7.0"
group = "net.coralmod.mod"

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.loader)
    modImplementation(libs.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.gson)

    implementation("net.raphimc:MinecraftAuth:5.0.0")
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

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}