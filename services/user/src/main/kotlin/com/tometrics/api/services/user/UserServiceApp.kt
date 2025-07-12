package com.tometrics.api.services.user

import com.tometrics.api.common.domain.models.ErrorResponse
import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.common.to
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.user.domain.models.*
import com.tometrics.api.services.user.routes.authRoutes
import com.tometrics.api.services.user.routes.geolocationRoutes
import com.tometrics.api.services.user.routes.userRoutes
import com.tometrics.api.services.user.services.DefaultUserGrpcService
import io.github.smiley4.ktoropenapi.route
import io.grpc.ServerBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication
import org.koin.java.KoinJavaComponent.get

fun main(args: Array<String>) {
    val serviceInfo = ServiceInfo(
        prefix = "/user",
        host = "localhost",
        port = 8082,
        type = ServiceType.USER,
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
    }.start(wait = false)

    var attempt = 0
    while (true) {
        try {
            val grpcPort = serviceInfo.grpcPort
            val grpcService: DefaultUserGrpcService = get(DefaultUserGrpcService::class.java)
            val grpcServer = ServerBuilder
                .forPort(grpcPort)
                .addService(grpcService)
                .build()
                .start()

            println("gRPC started on port $grpcPort")
            grpcServer.awaitTermination()
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("gRPC stopped")
            Thread.sleep(2000L * attempt++)
        }
    }
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
                "classpath:com/tometrics/api/services/user/db/migration",
            ),
            appModule,
            serviceModule,
            databaseModule,
            externalModule,
        )
    }

val configureStatusPages: StatusPagesConfig.() -> Unit
    get() = {
        exception<ServiceError> { call, cause ->
            val (status, message, code) = when (cause) {
                is UserIdsNotFoundError -> HttpStatusCode.BadRequest to "Missing user ids: ${cause.missingIds}" to "USER_IDS_NOT_FOUND"
                is ConflictException -> HttpStatusCode.Conflict to cause.message to "TODO"
                is UnauthorizedException -> HttpStatusCode.Unauthorized to cause.message to "TODO"
                is BadRequestException -> HttpStatusCode.BadRequest to cause.message to "TODO"
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message, code = code)
            call.respond(status, error)
        }
    }

fun Application.configureRouting() {
    routing {
        // TODO(aromano): check how to properly do health checks on services
        get("/user/health") {
            call.respond(HttpStatusCode.OK)
        }
        route("/api/v1") {
            authRoutes()
            geolocationRoutes()
            userRoutes()
        }
    }
}
