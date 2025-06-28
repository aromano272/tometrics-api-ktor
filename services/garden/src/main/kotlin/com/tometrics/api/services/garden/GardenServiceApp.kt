package com.tometrics.api.services.garden

import com.tometrics.api.auth.configureSecurity
import com.tometrics.api.common.domain.models.CommonError
import com.tometrics.api.common.domain.models.ErrorResponse
import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.common.to
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.services.garden.domain.models.*
import com.tometrics.api.services.garden.routes.authRoutes
import com.tometrics.api.services.garden.routes.geolocationRoutes
import com.tometrics.api.services.garden.routes.gardenRoutes
import com.tometrics.api.services.garden.services.GardenGrpcService
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.route
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.github.smiley4.schemakenerator.swagger.data.RefType
import io.grpc.ServerBuilder
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit {
    embeddedServer(
        factory = Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        },
    ) {
        module()
    }.start(wait = false)

    var attempt = 0
    while (true) {
        try {
            val grpcService: GardenGrpcService = get(GardenGrpcService::class.java)
            val grpcServer = ServerBuilder
                .forPort(9086)
                .addService(grpcService)
                .build()
                .start()

            println("gRPC started on port 9086")
            grpcServer.awaitTermination()
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("gRPC stoped on port 9086")
            Thread.sleep(2000L * attempt++)
        }
    }
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
                "classpath:com/tometrics/api/services/garden/db/migration",
            ),
            appModule(this@configureDI),
            serviceModule(this@configureDI),
            databaseModule,
            externalModule,
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
        exception<ServiceError> { call, cause ->
            val (status, message, code) = when (cause) {
                is GardenIdsNotFoundError -> HttpStatusCode.BadRequest to "Missing user ids: ${cause.missingIds}" to "USER_IDS_NOT_FOUND"
                is ConflictException -> HttpStatusCode.Conflict to cause.message to "TODO"
                is UnauthorizedException -> HttpStatusCode.Unauthorized to cause.message to "TODO"
                is BadRequestException -> HttpStatusCode.BadRequest to cause.message to "TODO"
            }

            call.application.environment.log.warn("Handled error", cause)
            val error = ErrorResponse(message, code = code)
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
        route("/api/v1") {
        }
    }
}
