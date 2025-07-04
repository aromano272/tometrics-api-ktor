package com.tometrics.api.services.socialgraph

import com.tometrics.api.common.domain.models.ErrorResponse
import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.socialgraph.domain.models.ServiceError
import com.tometrics.api.services.socialgraph.routes.socialGraphRoutes
import com.tometrics.api.services.socialgraph.service.DefaultSocialGraphGrpcService
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

fun main(args: Array<String>): Unit {
    val serviceInfo = ServiceInfo(
        prefix = "/socialgraph",
        host = "localhost",
        port = 8081,
        type = ServiceType.SOCIALGRAPH,
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
            val grpcService: DefaultSocialGraphGrpcService = get(DefaultSocialGraphGrpcService::class.java)
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
                "classpath:com/tometrics/api/services/socialgraph/db/migration",
            ),
            serviceModule,
            databaseModule,
        )
    }

val configureStatusPages: StatusPagesConfig.() -> Unit
    get() = {
        exception<ServiceError> { call, cause ->
            val (status, message) = when (cause) {
                else -> HttpStatusCode.InternalServerError to "Internal server error"
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message)
            call.respond(status, error)
        }
    }

fun Application.configureRouting() {
    routing {
        route("/api/v1/socialgraph") {
            socialGraphRoutes()
        }
    }
}
