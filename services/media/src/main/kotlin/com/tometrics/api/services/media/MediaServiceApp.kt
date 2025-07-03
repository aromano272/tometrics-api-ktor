package com.tometrics.api.services.media

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.media.routes.mediaRoutes
import com.tometrics.api.services.media.services.DefaultMediaGrpcService
import io.github.smiley4.ktoropenapi.route
import io.grpc.ServerBuilder
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication
import org.koin.java.KoinJavaComponent.get

fun main(args: Array<String>) {
    val serviceInfo = ServiceInfo(
        prefix = "/media",
        host = "localhost",
        port = 8087,
        type = ServiceType.MEDIA,
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
            val grpcService: DefaultMediaGrpcService = get(DefaultMediaGrpcService::class.java)
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