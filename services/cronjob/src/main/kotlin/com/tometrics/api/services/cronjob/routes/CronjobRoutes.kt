package com.tometrics.api.services.cronjob.routes

import com.tometrics.api.services.cronjob.services.CronjobService
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
                // NOCHECKIN:
//                cronjobService.checkForDailyHarvests()
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}