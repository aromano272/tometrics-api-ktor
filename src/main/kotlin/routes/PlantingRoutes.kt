package com.sproutscout.api.routes

import com.sproutscout.api.domain.models.requireRequester
import com.sproutscout.api.model.PlantId
import com.sproutscout.api.routes.models.routes.models.GetAllPlantingsResponse
import com.sproutscout.api.service.GardenService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.plantingRoutes(
    gardenService: GardenService,
) {
    authenticate {
        route("/planting") {
            get("/all") {
                val requester = call.requireRequester()
                val plantings = gardenService.getAll(requester, )
                call.respond(GetAllPlantingsResponse(plantings))
            }

            delete("/{id}") {
                val requester = call.requireRequester()
                val id = call.pathParameters["id"]?.toIntOrNull()!!
                gardenService.delete(requester, id)
                call.respond(HttpStatusCode.OK)
            }

            patch("/{id}") {
                val requester = call.requireRequester()
                val id = call.pathParameters["id"]?.toIntOrNull()!!
                val request = call.receive<PatchPlantingRequest>()
                val updatedPlanting = gardenService.update(requester, id, request.newQuantity)
                call.respond(updatedPlanting)
            }

            post("/add") {
                val requester = call.requireRequester()
                val request = call.receive<AddPlantingRequest>()
                val planting = gardenService.add(requester, request.plantId, request.quantity)
                call.respond(planting)
            }

        }
    }
}

@Serializable
data class PatchPlantingRequest(
    val newQuantity: Int,
)

@Serializable
data class AddPlantingRequest(
    val plantId: PlantId,
    val quantity: Int,
)



