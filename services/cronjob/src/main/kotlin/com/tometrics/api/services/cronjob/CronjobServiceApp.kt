package com.tometrics.api.services.cronjob

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.cronjob.routes.cronjobRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    commonModule(
        serviceInfo = ServiceInfo(
            prefix = "/cronjob",
            host = "localhost",
            port = this.environment.config.port,
            type = ServiceType.CRONJOB,
        ),
        configureDI = configureDI,
    )
    configureRouting()
}

val configureDI: KoinApplication.() -> Unit
    get() = {
        modules(
            serviceModule,
        )
    }

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            cronjobRoutes()
        }
    }
}
