package com.sproutscout.api

import com.sproutscout.api.domain.models.*
import com.sproutscout.api.routes.authRoutes
import com.sproutscout.api.routes.plantRoutes
import com.sproutscout.api.routes.plantingRoutes
import com.sproutscout.api.service.AuthService
import com.sproutscout.api.service.GardenService
import com.sproutscout.api.service.PlantService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            val (status, message) = when (cause) {
                is NotFoundException -> HttpStatusCode.NotFound to cause.message
                is UnauthorizedException -> HttpStatusCode.Unauthorized to cause.message
                is ConflictException -> HttpStatusCode.Conflict to cause.message
                is BadRequestException -> HttpStatusCode.BadRequest to cause.message
                is ForbiddenException -> HttpStatusCode.Forbidden to cause.message
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message ?: "Unknown error")
            call.respond(status, error)
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)

            val error = ErrorResponse("Unexpected error")
            call.respond(HttpStatusCode.InternalServerError, error)
        }
    }
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello"))
                ValidationResult.Invalid("Body text should start with 'Hello'")
            else ValidationResult.Valid
        }
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    val authService: AuthService by inject()
    val plantService: PlantService by inject()
    val gardenService: GardenService by inject()
    val logger: Logger by inject()


    routing {
        route("/api/v1") {
            authRoutes(authService, logger)
            plantRoutes(plantService)
            plantingRoutes(gardenService)
        }
    }
}
