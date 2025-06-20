package com.tometrics.api.services.socialgraph

import com.tometrics.api.auth.configureSecurity
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.services.socialgraph.routes.socialGraphRoutes
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.github.smiley4.schemakenerator.swagger.data.RefType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jdbi.v3.core.Jdbi
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureDI()
    configureMonitoring()
    configureSecurity()
    configureSerialization()
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
        )
    }

    // Initialize database
    getKoin().get<Jdbi> { parametersOf(environment) }
}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
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
//    install(StatusPages) {
//        exception<ApiException> { call, cause ->
//            val (status, message) = when (cause) {
//                is NotFoundException -> HttpStatusCode.NotFound to cause.message
//                is UnauthorizedException -> HttpStatusCode.Unauthorized to cause.message
//                is ConflictException -> HttpStatusCode.Conflict to cause.message
//                is BadRequestException -> HttpStatusCode.BadRequest to cause.message
//                is ForbiddenException -> HttpStatusCode.Forbidden to cause.message
//            }
//
//            call.application.environment.log.warn("Handled error", cause)
//            val error = ErrorResponse(message ?: "Unknown error")
//            call.respond(status, error)
//        }
//        exception<Throwable> { call, cause ->
//            call.application.environment.log.error("Unhandled exception", cause)
//
//            val error = ErrorResponse("Unexpected error")
//            call.respond(HttpStatusCode.InternalServerError, error)
//        }
//    }
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
        route("/api/v1/socialgraph") {
            socialGraphRoutes()
        }
    }
}
