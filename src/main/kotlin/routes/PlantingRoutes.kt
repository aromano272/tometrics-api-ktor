package com.tometrics.api.routes

import com.tometrics.api.domain.models.BadRequestException
import com.tometrics.api.domain.models.Planting
import com.tometrics.api.domain.models.requireRequester
import com.tometrics.api.routes.models.AddPlantingRequest
import com.tometrics.api.routes.models.GetAllPlantingsResponse
import com.tometrics.api.routes.models.PatchPlantingRequest
import com.tometrics.api.service.GardenService
import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.plantingRoutes() {
    val gardenService: GardenService by inject()

    authenticate {
        route("/planting", {
            tags = listOf("Planting")
        }) {

            get("/all", {
                description = "Retrieve all plantings for the authenticated user"
                response {
                    HttpStatusCode.OK to {
                        description = "List of all plantings"
                        body<GetAllPlantingsResponse>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val plantings = gardenService.getAll(requester)
                call.respond(GetAllPlantingsResponse(plantings))
            }

            delete("/{id}", {
                description = "Delete a planting by its ID"
                request {
                    pathParameter<Int>("id") {
                        description = "The ID of the planting to delete"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Planting successfully deleted"
                    }
                    HttpStatusCode.NotFound to {
                        description = "Planting not found"
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Planting doesn't belong to this user"
                    }
                }
            }) {
                val requester = call.requireRequester()
                val id = call.pathParameters["id"]?.toIntOrNull()!!
                gardenService.delete(requester, id)
                call.respond(HttpStatusCode.OK)
            }

            patch("/{id}", {
                description = "Update the quantity of a planting by its ID"
                request {
                    pathParameter<Int>("id") {
                        description = "The ID of the planting to update"
                    }
                    body<PatchPlantingRequest> {
                        description = "Request containing the new quantity for the planting"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The updated planting"
                        body<Planting>()
                    }
                    HttpStatusCode.NotFound to {
                        description = "Planting not found"
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Planting doesn't belong to this user"
                    }
                }
            }) {
                val requester = call.requireRequester()
                val id = call.pathParameters["id"]?.toIntOrNull()!!
                val request = call.receive<PatchPlantingRequest>()
                val updatedPlanting = gardenService.update(
                    requester = requester,
                    id = id,
                    newQuantity = request.newQuantity,
                    newName = request.newName,
                    newDiary = request.newDiary,
                    newHarvested = request.newHarvested,
                )
                call.respond(updatedPlanting)
            }

            post("/add", {
                description = "Add a new planting"
                request {
                    body<AddPlantingRequest> {
                        description = "Request to add a new planting with plant ID and quantity"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The newly added planting"
                        body<Planting>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                // TODO if the wrong data comes in it throws a 500, it should print a nicer error and not a 500
                val request = call.receive<AddPlantingRequest>()
                if (request.quantity <= 0) throw BadRequestException("quantity needs to be greater than 0")

                val planting = gardenService.add(requester, request.plantId, request.quantity)
                call.respond(planting)
            }
        }
    }
}
