plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.google.protobuf)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":services:protos"))
    implementation(project(":common"))

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.grpc.netty)

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
