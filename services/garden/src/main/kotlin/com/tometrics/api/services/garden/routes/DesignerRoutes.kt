package com.tometrics.api.services.garden.routes

import com.tometrics.api.domain.models.GardenCellRef
import com.tometrics.api.domain.models.GardenDesign
import com.tometrics.api.service.DesignerService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.designerRoutes() {
    val designerService: DesignerService by inject()

    authenticate {
        route("/designer", {
            tags = listOf("Garden Designer")
        }) {
            get({
                description = "Get the current garden design layout"
                response {
                    HttpStatusCode.OK to {
                        description = "The current garden design"
                        body<GardenDesign> {
                            description = """
                                Garden design object containing:
                                - cells: 2D grid of garden cells with plant information
                                - plantedContiguous: Groups of connected cells with the same plant
                                - summary: Yield calculations for each plant type
                            """.trimIndent()
                        }
                    }
                }
            }) {
                val garden = designerService.get()
                call.respond(garden)
            }

            put({
                description = "Update the garden design layout"
                request {
                    body<List<List<GardenCellRef>>> {
                        description = """
                                2D grid of garden cell references where each cell contains:
                                - x: X coordinate (row)
                                - y: Y coordinate (column)
                                - plantId: ID of the plant to place in this cell (null for empty cells)
                            """.trimIndent()
                        example("2x2 Garden") {
                            value = """
                                [
                                  [{"x":0,"y":0,"plantId":1}, {"x":0,"y":1,"plantId":1}],
                                  [{"x":1,"y":0,"plantId":2}, {"x":1,"y":1,"plantId":null}]
                                ]
                                """.trimIndent()
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The updated garden design"
                        body<GardenDesign> {
                            description = "Updated garden design with new layout and calculations"
                        }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid cell coordinates or grid layout"
                    }
                    HttpStatusCode.NotFound to {
                        description = "One or more plant IDs not found"
                    }
                }
            }) {
                val cells = call.receive<List<List<GardenCellRef>>>()
                val result = designerService.update(cells)
                call.respond(result)
            }
        }
    }
}
