package com.tometrics.api.user

import com.tometrics.api.auth.configureSecurity
import com.tometrics.api.common.domain.models.CommonError
import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.user.domain.models.ApiException
import com.tometrics.api.user.domain.models.ErrorResponse
import com.tometrics.api.user.routes.internalRoutes
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.route
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.github.smiley4.schemakenerator.swagger.data.RefType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureMonitoring()
    configureSecurity()
    configureHTTP()
    configureRouting()
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            jdbiModule(
                "classpath:db/migration",
                "classpath:com/tometrics/api/db/migration",
            ),
            appModule,
            serviceModule,
            databaseModule,
        )
    }
}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}

fun Application.configureHTTP() {
    install(OpenApi) {
        spec("3.0.0") {}
        outputFormat = OutputFormat.YAML
        schemas {
            generator = SchemaGenerator.reflection {
                referencePath = RefType.OPENAPI_SIMPLE
            }
        }
    }

    routing {
        route("/openapi/api.yaml") {
            openApi()
        }
        route("/swagger") {
            swaggerUI("/openapi/api.yaml")
        }
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
}

fun Application.configureRouting() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            val (status, message) = when (cause) {
                else -> HttpStatusCode.InternalServerError to "Internal server error"
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message)
            call.respond(status, error)
        }
        exception<CommonError> { call, cause ->
            val (status, message) = when (cause) {
                is UnauthorizedError -> HttpStatusCode.Unauthorized to "Unauthorized"
                is ValidationError -> HttpStatusCode.BadRequest to "Validation errors"
            }
            val validationErrors = (cause as? ValidationError)?.errors

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message, validationErrors)
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
            explicitNulls = false
            encodeDefaults = true
        })
    }

    routing {
        route("/internal/user") {
            internalRoutes()
        }
        route("/api/v1/user") {
        }
    }
}
