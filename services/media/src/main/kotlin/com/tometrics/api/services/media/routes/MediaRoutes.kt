package com.tometrics.api.services.media.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.services.media.services.MediaService
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.mediaRoutes() {
    val mediaService: MediaService by inject()

    authenticate {
        route("/media") {
            get("/generate-presigned-url") {
                val requester = call.requireRequester()
                val url = mediaService.generatePresignedUrl(requester)
                call.respond(url)
            }
        }
    }
}
