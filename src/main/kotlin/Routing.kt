package com.sproutscout.api

import com.sproutscout.api.models.ApiException
import com.sproutscout.api.models.BadRequestException
import com.sproutscout.api.models.ConflictException
import com.sproutscout.api.models.ErrorResponse
import com.sproutscout.api.models.ForbiddenException
import com.sproutscout.api.models.NotFoundException
import com.sproutscout.api.models.UnauthorizedException
import com.sproutscout.api.routes.authRoutes
import com.sproutscout.api.routes.plantRoutes
import com.sproutscout.api.service.AuthService
import com.sproutscout.api.service.PlantService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.logging.Logger
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
    val logger: Logger by inject()


    routing {
        route("/api/v1") {
            authRoutes(authService, logger)
            plantRoutes(plantService)
        }
    }
}
