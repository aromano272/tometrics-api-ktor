package com.sproutscout.api.routes

import com.sproutscout.api.models.NotFoundException
import com.sproutscout.api.routes.models.GetAllPlantsResponse
import com.sproutscout.api.service.PlantService
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.plantRoutes(
    plantService: PlantService,
) {
    authenticate {
        route("/plant") {
            get("/all") {
                val plants = plantService.getAll()
                call.respond(GetAllPlantsResponse(plants))
            }

            get("/{id}") {
                val plantId = call.pathParameters["id"]?.toIntOrNull()!!
                val plant = plantService.getById(plantId) ?: throw NotFoundException("Plant #$plantId not found")
                call.respond(plant)
            }
        }
    }
}

