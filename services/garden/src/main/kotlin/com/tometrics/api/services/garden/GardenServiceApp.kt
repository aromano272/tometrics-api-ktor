package com.tometrics.api.services.garden

import com.tometrics.api.common.domain.models.ErrorResponse
import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.common.to
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.garden.domain.models.ServiceError
import com.tometrics.api.services.garden.routes.designerRoutes
import com.tometrics.api.services.garden.routes.harvestRoutes
import com.tometrics.api.services.garden.routes.plantRoutes
import com.tometrics.api.services.garden.routes.plantingRoutes
import com.tometrics.api.services.garden.services.DefaultGardenGrpcService
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
    var serviceInfo: ServiceInfo? = null
    embeddedServer(
        factory = Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        },
    ) {
        serviceInfo = ServiceInfo(
            prefix = "/garden",
            host = "localhost",
            port = this.environment.config.port,
            type = ServiceType.GARDEN,
        )
        module(serviceInfo)
    }.start(wait = false)

    var attempt = 0
    while (true) {
        try {
            val grpcService: DefaultGardenGrpcService = get(DefaultGardenGrpcService::class.java)
            val grpcServer = ServerBuilder
                .forPort(serviceInfo!!.grpcPort)
                .addService(grpcService)
                .build()
                .start()

            println("gRPC started on port 9086")
            grpcServer.awaitTermination()
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("gRPC stoped on port 9086")
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
                "classpath:com/tometrics/api/services/garden/db/migration",
            ),
            serviceModule,
            databaseModule,
        )
    }

val configureStatusPages: StatusPagesConfig.() -> Unit
    get() = {
        exception<ServiceError> { call, cause ->
            val (status, message, code) = when (cause) {
                else -> HttpStatusCode.InternalServerError to "" to ""
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message, code = code)
            call.respond(status, error)
        }
    }

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            designerRoutes()
            harvestRoutes()
            plantingRoutes()
            plantRoutes()
        }
    }
}
