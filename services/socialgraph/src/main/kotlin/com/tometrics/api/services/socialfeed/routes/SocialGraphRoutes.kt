package com.tometrics.api.services.socialfeed.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.services.socialfeed.service.SocialGraphService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.socialGraphRoutes() {
    val socialGraphService: SocialGraphService by inject()

    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    authenticate {

        get("/connections") {
            val requester = call.requireRequester()
            val connections = socialGraphService.getConnectionsByUserId(requester.userId)
            call.respond(connections)
        }

        get("/connections/{userId}") {
            val userId = call.pathParameters["userId"]?.toIntOrNull() ?: throw ValidationError(
                listOf(
                    "`userId` required"
                )
            )
            val connections = socialGraphService.getConnectionsByUserId(userId)
            call.respond(connections)
        }

        post("/follow/{userId}") {
            val requester = call.requireRequester()
            val userId = call.pathParameters["userId"]?.toIntOrNull() ?: throw ValidationError(
                listOf(
                    "`userId` required"
                )
            )
            socialGraphService.follow(requester.userId, userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/follow/{userId}") {
            val requester = call.requireRequester()
            val userId = call.pathParameters["userId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                "`userId` required"
            ))
            socialGraphService.unfollow(requester.userId, userId)
            call.respond(HttpStatusCode.OK)
        }

    }
}
