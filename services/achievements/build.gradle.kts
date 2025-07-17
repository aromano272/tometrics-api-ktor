plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.tometrics.api.services.achievements"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springGrpcVersion"] = "0.9.0"

dependencies {
    implementation(project(":db"))
    implementation(project(":auth"))
    implementation(project(":common"))
    implementation(project(":services:commongrpc"))
    implementation(project(":services:commonservice"))

//    implementation(libs.logback.classic)
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.sqlobject)
    implementation(libs.jdbi.jackson2)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.dotenv)
    implementation(libs.openapi.generator)
    implementation(libs.swagger.ui)
    implementation(libs.schema.kenerator.swagger)
    implementation(libs.rabbitmq)

    implementation(libs.spring.boot.amqp)
    implementation(libs.spring.boot.jdbc)
    implementation(libs.spring.boot.security)
    implementation(libs.auth0.jwt)
    implementation(libs.spring.boot.web)
    implementation(libs.grpc.java.services)
    implementation(libs.spring.boot.grpc)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers.test)
    testImplementation(libs.spring.boot.rabbit.test)
    testImplementation(libs.spring.boot.grpc.test)
    testImplementation(libs.spring.boot.restdocs.test)
    testImplementation(libs.spring.boot.security.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(project(":services:commonservicetest"))
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(libs.testcontainers.rabbitmq)
    testImplementation(libs.mockk)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}

tasks.test {
    useJUnitPlatform()
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}
