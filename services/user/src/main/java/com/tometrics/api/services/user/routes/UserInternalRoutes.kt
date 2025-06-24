package com.tometrics.api.services.user.routes

import com.tometrics.api.services.user.routes.models.ValidateUsersRequest
import com.tometrics.api.services.user.services.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userInternalRoutes() {
    val userService: UserService by inject()

    post("/validate-users") {
        val request = call.receive<ValidateUsersRequest>()
        userService.validateUserIds(request.userIds.toSet())
        call.respond(HttpStatusCode.OK)
    }
}
