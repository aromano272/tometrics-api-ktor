plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    implementation(libs.dotenv)
}