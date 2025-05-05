package com.tometrics.api.routes

import com.tometrics.api.service.CronjobService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.cronjobRoutes() {
    val cronjobService: CronjobService by inject()

    authenticate("auth-cronjob") {
        route("/cronjob") {

            post("daily-harvest") {
                cronjobService.checkForDailyHarvests()
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}