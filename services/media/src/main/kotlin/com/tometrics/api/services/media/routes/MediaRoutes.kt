package com.tometrics.api.services.media.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.services.media.services.MediaService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.mediaRoutes() {
    val mediaService: MediaService by inject()

    authenticate {
        route("/media", {
            tags = listOf("Media")
        }) {
            get("/generate-presigned-url", {
                description = "Generates a presigned URL for uploading media files."
                response {
                    HttpStatusCode.OK to {
                        description = "A response containing a presigned URL for S3 upload."
                        body<GeneratePresignedUrlResponse>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val url = mediaService.generatePresignedUrl(requester)
                call.respond(GeneratePresignedUrlResponse(url))
            }
        }
    }
}

@Serializable
data class GeneratePresignedUrlResponse(
    val url: String,
)
