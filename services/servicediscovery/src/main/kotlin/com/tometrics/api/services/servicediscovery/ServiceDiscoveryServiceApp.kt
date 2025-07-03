package com.tometrics.api.services.servicediscovery

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.servicediscovery.service.DefaultServiceDiscoveryGrpcService
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.grpc.ServerBuilder
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication
import org.koin.java.KoinJavaComponent.get

fun main(args: Array<String>) {
    val serviceInfo = ServiceInfo(
        prefix = "/servicediscovery",
        host = "localhost",
        port = 8083,
        type = ServiceType.SERVICEDISCOVERY,
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
            val grpcService: DefaultServiceDiscoveryGrpcService = get(DefaultServiceDiscoveryGrpcService::class.java)
            val grpcServer = ServerBuilder
                .forPort(9083)
                .addService(grpcService)
                .build()
                .start()

            println("gRPC started on port 9083")
            grpcServer.awaitTermination()
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("gRPC stoped on port 9083")
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
            serviceModule,
        )
    }

fun Application.configureRouting() {
    routing {
        route("/servicediscovery") {
            route("/openapi/api.yaml") {
                openApi()
            }
            route("/swagger") {
                swaggerUI("/openapi/api.yaml")
            }

        }
    }
}
