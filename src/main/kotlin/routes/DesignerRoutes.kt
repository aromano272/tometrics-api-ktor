package com.tometrics.api.routes

import com.tometrics.api.domain.models.GardenCellRef
import com.tometrics.api.service.DesignerService
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.designerRoutes(
    designerService: DesignerService
) {
    authenticate {
        route("/designer") {

            get {
                val garden = designerService.get()
                call.respond(garden)
            }

            put {
                val cells = call.receive<List<List<GardenCellRef>>>()
                designerService.update(cells)
            }

        }
    }
}