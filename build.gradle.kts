plugins {
    id("java")
    alias(libs.plugins.loom)
}

version = "0.8.4"
group = "net.cacaovisualclient.mod"

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.loader)
    modImplementation(libs.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.gson)

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
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
