package com.tometrics.api.servicediscovery.routes

import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.servicediscovery.service.Service
import com.tometrics.api.servicediscovery.service.ServiceInfo
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.serviceDiscoveryRoutes() {
    val service: Service by inject()

    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    get("/service/{name}") {
        val name = call.pathParameters["name"] ?: throw ValidationError(listOf(
            "`name` is required"
        ))
        val info = service.get(name) ?: throw NotFoundException()
        call.respond(info)
    }

    post("/service") {
        val request = call.receive<PostServiceRequest>()
        val info = ServiceInfo(request.name, request.host, request.port)
        service.register(info)
        call.respond(HttpStatusCode.OK)
    }

}

@Serializable
data class PostServiceRequest(
    val name: String,
    val host: String,
    val port: Int,
)
