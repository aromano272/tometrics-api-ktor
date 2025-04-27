package com.sproutscout.api.routes

import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantYield
import com.sproutscout.api.model.Vegetable
import com.sproutscout.api.models.Millis
import com.sproutscout.api.models.NotFoundException
import com.sproutscout.api.routes.models.GetAllPlantsResponse
import com.sproutscout.api.service.PlantService
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.time.Instant

fun Route.gardenRoutes(
    gardenService: GardenService,
) {
    authenticate {
        route("/garden") {
            get {
                call.respond(GetAllPlantsResponse(Vegetable.entries.map { it.plant }))
            }

            route("/planting") {
                get("/{id}") {

                }
            }
            get("/{id}") {
                val plantId = call.pathParameters["id"]?.toIntOrNull()!!
                val plant = Vegetable.entries.map { it.plant }
                    .find { it.id == plantId }
                    ?: throw NotFoundException("Plant #$plantId not found")
                call.respond(plant)
            }
        }
    }
}

typealias PlantingId = Int

@Serializable
data class Planting(
    val id: PlantingId,
    val plant: Plant,
    val plantingAreaM2: Int,
    val totalYield: PlantYield,
    val createdAt: Millis,
)

