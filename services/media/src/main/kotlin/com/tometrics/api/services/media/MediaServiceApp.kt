package com.tometrics.api.services.media

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.media.routes.mediaRoutes
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
        serviceInfo = ServiceInfo(
            prefix = "/media",
            host = "localhost",
            port = this.environment.config.port,
            type = ServiceType.MEDIA,
        ),
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
            mediaRoutes()
        }
    }
}