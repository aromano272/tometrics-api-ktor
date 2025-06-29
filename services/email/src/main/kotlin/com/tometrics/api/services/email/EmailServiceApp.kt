package com.tometrics.api.services.email

import com.tometrics.api.services.commonservice.commonModule
import io.github.smiley4.ktoropenapi.route
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    commonModule(
        serviceUrlPrefix = "/email",
        configureDI = configureDI,
    )
    configureRouting()
}

val configureDI: KoinApplication.() -> Unit
    get() = {
        modules(
            appModule,
            serviceModule,
        )
    }

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
        }
    }
}