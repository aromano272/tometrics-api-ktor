plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kotlin.plugin.krpc)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":services:userrpc"))
    implementation(project(":services:userclient"))

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlin.krpc.core)
    implementation(libs.kotlin.krpc.ktor.client)
    implementation(libs.kotlin.krpc.serialization.json)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.dotenv)

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)
}
