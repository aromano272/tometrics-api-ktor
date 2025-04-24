package com.sproutscout.api.routes

import com.sproutscout.api.service.JobService
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.jobRoutes(
    jobService: JobService,
) {
    route("/jobs") {
        authenticate(optional = true) {

        }

        authenticate {
            
        }
    }
}