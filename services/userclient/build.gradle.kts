plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // TODO(aromano): 
//    idea: each service has an internal routing that has a specific bearer token, just like cron service
//    this way we limit access to the outside for internal-only api
//    i think we're still gonna have problems with nginx because we want to allow services to communicate internally
//    on the /internal route with http(not https), so ill probably need to modify nginx to enable this
    implementation(project(":common"))

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.dotenv)
    implementation(libs.openapi.generator)

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)
}
