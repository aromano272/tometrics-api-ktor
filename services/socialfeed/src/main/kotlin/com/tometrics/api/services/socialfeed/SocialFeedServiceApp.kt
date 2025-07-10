package com.tometrics.api.services.socialfeed

import com.tometrics.api.common.domain.models.ErrorResponse
import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.socialfeed.domain.models.*
import com.tometrics.api.services.socialfeed.routes.socialFeedRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication

fun main(args: Array<String>) {
    val serviceInfo = ServiceInfo(
        prefix = "/socialfeed",
        host = "localhost",
        port = 8088,
        type = ServiceType.SOCIALFEED,
    )
    embeddedServer(
        factory = Netty,
        configure = {
            connector {
                host = serviceInfo.host
                port = serviceInfo.port
            }
        },
    ) {
        module(serviceInfo)
    }.start(wait = true)
}

fun Application.module(serviceInfo: ServiceInfo) {
    commonModule(
        serviceInfo = serviceInfo,
        configureDI = configureDI,
        configureStatusPages = configureStatusPages,
    )
    configureRouting()
}

val configureDI: KoinApplication.() -> Unit
    get() = {
        modules(
            jdbiModule(
                "classpath:db/migration",
                "classpath:com/tometrics/api/services/socialfeed/db/migration",
            ),
            serviceModule,
            databaseModule,
        )
    }

val configureStatusPages: StatusPagesConfig.() -> Unit
    get() = {
        exception<ServiceError> { call, cause ->
            val (status, message) = when (cause) {
                InvalidMediaUrls -> HttpStatusCode.BadRequest to "Invalid image urls"
                CreatePostFailed,
                is LocationNotFound,
                is PostNotFound,
                is UserNotFound -> HttpStatusCode.InternalServerError to "Internal server error"
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message)
            call.respond(status, error)
        }
    }

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            socialFeedRoutes()
        }
    }
}
