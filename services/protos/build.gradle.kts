plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.google.protobuf)
}

group = "com.tometrics.api"
version = "0.0.1"

repositories {
    mavenCentral()
}

protobuf {
    protoc {
        artifact = libs.protoc.asProvider().get().toString()
    }
    plugins {
        create("grpc") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        create("grpckt") {
            artifact = libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

dependencies {
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.protobuf)
    api(libs.protobuf.kotlin)

//    api(libs.grpc.kotlin.stub)
//    api(libs.grpc.protobuf)
//    api(libs.protobuf.java.util)
//    api(libs.protobuf.kotlin)
//    api(libs.grpc.kotlin.stub)

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}
