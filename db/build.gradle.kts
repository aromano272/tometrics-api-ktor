plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.postgresql)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.kotlin)
    implementation(libs.jdbi.sqlobject)
    implementation(libs.jdbi.jackson2)
    implementation(libs.jackson.kotlin)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.dotenv)

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(libs.mockk)
}