package com.tometrics.api.user.routes

import com.tometrics.api.user.routes.models.ValidateUsersRequest
import com.tometrics.api.user.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.internalRoutes() {
    val userService: UserService by inject()

    authenticate("auth-internal") {

        post("/validate-users") {
            val request = call.receive<ValidateUsersRequest>()
            userService.validateUserIds(request.userIds.toSet())
            call.respond(HttpStatusCode.OK)
        }

    }
}
