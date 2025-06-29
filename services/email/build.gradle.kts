plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.google.protobuf)
}

group = "com.tometrics.api"
version = "0.0.1"

application {
    mainClass = "com.tometrics.api.services.email.EmailServiceAppKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":auth"))
    implementation(project(":common"))
    implementation(project(":services:commongrpc"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.mustache)

    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.server.config.yaml)

    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    implementation(libs.dotenv)
    implementation(libs.openapi.generator)
    implementation(libs.swagger.ui)
    implementation(libs.schema.kenerator.swagger)

    implementation(libs.grpc.netty)

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(libs.mockk)
}