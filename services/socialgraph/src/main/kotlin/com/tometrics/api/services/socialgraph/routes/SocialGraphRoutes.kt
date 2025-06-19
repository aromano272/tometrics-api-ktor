package com.tometrics.api.services.socialgraph.routes

import com.tometrics.api.services.socialgraph.service.SocialGraphService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.socialGraphRoutes() {
    val socialGraphService: SocialGraphService by inject()

    get {
        call.respond(HttpStatusCode.OK, mapOf("message" to "Social Graph API"))
    }

    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

}
