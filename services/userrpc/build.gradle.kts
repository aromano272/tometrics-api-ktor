plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kotlin.plugin.krpc)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlin.krpc.server)
}