package com.tometrics.api.routes

import com.tometrics.api.domain.models.requireRequester
import com.tometrics.api.routes.models.AddHarvestRequest
import com.tometrics.api.service.HarvestService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.harvestRoutes() {
    val harvestService: HarvestService by inject()

    authenticate {
        route("/harvest") {

            post("/add") {
                val requester = call.requireRequester()
                val request = call.receive<AddHarvestRequest>()

                val harvest = harvestService.add(
                    requester = requester,
                    plantingId = request.plantingId,
                    quantity = request.quantity,
                    unit = request.unit,
                    createdAt = request.createdAt,
                )
                call.respond(harvest)
            }

            get("/all") {
                val requester = call.requireRequester()
                val plantingId = call.request.queryParameters["plantingId"]?.toIntOrNull()
                val plantId = call.request.queryParameters["plantId"]?.toIntOrNull()

                val harvests = harvestService.getAll(requester, plantingId, plantId)
                call.respond(harvests)
            }

            delete("/{id}") {
                val requester = call.requireRequester()
                val harvestId = call.pathParameters["id"]?.toIntOrNull()!!

                harvestService.delete(requester, harvestId)
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}