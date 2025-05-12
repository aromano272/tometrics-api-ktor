package com.tometrics.api.routes

import com.tometrics.api.domain.models.HarvestRecord
import com.tometrics.api.domain.models.requireRequester
import com.tometrics.api.routes.models.AddHarvestRequest
import com.tometrics.api.service.HarvestService
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.harvestRoutes() {
    val harvestService: HarvestService by inject()

    authenticate {
        route("/harvest", {
            tags = listOf("Harvest")
        }) {

            post("/add", {
                description = "Creates a new harvest record for a specific planting."
                request {
                    body<AddHarvestRequest> {
                        description = "Details of the harvest to be added."
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The created harvest record."
                        body<HarvestRecord>()
                    }
                }
            }) {
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

            get("/all", {
                description = "Retrieves all harvest records, optionally filtered by planting or plant ID."
                request {
                    queryParameter<Int>("plantingId") {
                        description = "Filter by planting ID."
                        required = false
                    }
                    queryParameter<Int>("plantId") {
                        description = "Filter by plant ID."
                        required = false
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "A list of harvest records."
                        body<List<HarvestRecord>>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val plantingId = call.request.queryParameters["plantingId"]?.toIntOrNull()
                val plantId = call.request.queryParameters["plantId"]?.toIntOrNull()

                val harvests = harvestService.getAll(requester, plantingId, plantId)
                call.respond(harvests)
            }

            delete("/{id}", {
                summary = "Delete a harvest"
                description = "Deletes a specific harvest record by its ID."
                request {
                    pathParameter<Int>("id") {
                        description = "The ID of the harvest to delete."
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Indicates the harvest was successfully deleted."
                    }
                }
            }) {
                val requester = call.requireRequester()
                val harvestId = call.pathParameters["id"]?.toIntOrNull()!!

                harvestService.delete(requester, harvestId)
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}