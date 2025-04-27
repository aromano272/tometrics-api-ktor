package com.sproutscout.api.routes

import com.sproutscout.api.model.Plant
import com.sproutscout.api.models.NotFoundException
import com.sproutscout.api.routes.models.GetAllPlantsResponse
import com.sproutscout.api.service.PlantService
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.plantRoutes(
    plantService: PlantService,
) {
    authenticate {
        route("/plant") {
            get("/all", {
                description = "Retrieve all plants"
                response {
                    HttpStatusCode.OK to {
                        description = "List of all plants"
                        body<GetAllPlantsResponse>()
                    }
                }
            }) {
                val plants = plantService.getAll()
                call.respond(GetAllPlantsResponse(plants))
            }

            get("/{id}", {
                description = "Retrieve a plant by its ID"
                request {
                    pathParameter<Int>("id") {
                        description = "The ID of the plant to retrieve"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The plant with the specified ID"
                        body<Plant>()
                    }
                    HttpStatusCode.NotFound to {
                        description = "Plant not found"
                    }
                }
            }) {
                val plantId = call.pathParameters["id"]?.toIntOrNull()!!
                val plant = plantService.getById(plantId) ?: throw NotFoundException("Plant #$plantId not found")
                call.respond(plant)
            }
        }
    }
}
