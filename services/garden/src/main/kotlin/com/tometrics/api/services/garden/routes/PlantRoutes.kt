package com.tometrics.api.services.garden.routes

import com.tometrics.api.services.garden.domain.models.Plant
import com.tometrics.api.services.garden.routes.models.GetAllPlantsResponse
import com.tometrics.api.services.garden.services.PlantService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.plantRoutes() {
    val plantService: PlantService by inject()

    authenticate {
        route("/plant", {
            tags = listOf("Plant")
        }) {
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
                val plant = plantService.getById(plantId)
                call.respond(plant)
            }
        }
    }
}
