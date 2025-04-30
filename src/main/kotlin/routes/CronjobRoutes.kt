package com.sproutscout.api.routes

import com.sproutscout.api.service.CronjobService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cronjobRoutes(
    cronjobService: CronjobService,
) {
    authenticate("auth-cronjob") {
        route("/cronjob") {

            post("daily-harvest") {
                cronjobService.checkForDailyHarvests()
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}