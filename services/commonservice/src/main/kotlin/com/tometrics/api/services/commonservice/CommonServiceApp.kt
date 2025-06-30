package com.tometrics.api.services.commonservice

import com.tometrics.api.auth.configureSecurity
import com.tometrics.api.common.domain.models.*
import com.tometrics.api.services.commongrpc.commonServicesGrpcModule
import com.tometrics.api.services.commongrpc.services.ServiceDiscoveryGrpcClient
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
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun Application.commonModule(
    serviceInfo: ServiceInfo,
    configureDI: KoinApplication.() -> Unit,
    configureMonitoring: CallLoggingConfig.() -> Unit = {},
    configureStatusPages: StatusPagesConfig.() -> Unit = {},
) {
    commonConfigureDI(configureDI)
    commonConfigureMonitoring(configureMonitoring)
    configureSecurity()
    commonConfigureHTTP(serviceInfo.prefix)
    commonConfigureStatusPages(configureStatusPages)
    commonContentNegotiation()

    val serviceDiscoveryGrpcClient: ServiceDiscoveryGrpcClient = get()
    monitor.subscribe(ApplicationStarted) {
        launch {
            serviceDiscoveryGrpcClient.register(serviceInfo)
        }
    }
}

fun Application.commonConfigureDI(
    block: KoinApplication.() -> Unit,
) {
    install(Koin) {
        slf4jLogger()
        modules(
            appModule(this@commonConfigureDI),
            commonServicesGrpcModule,
        )
        block()
    }
}

fun Application.commonConfigureMonitoring(
    block: CallLoggingConfig.() -> Unit,
) {
    install(CallLogging) {
        level = Level.INFO
        block()
    }
}

fun Application.commonConfigureHTTP(
    serviceUriPrefix: String,
) {
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
        route("$serviceUriPrefix/openapi/api.yaml") {
            openApi()
        }
        route("$serviceUriPrefix/swagger") {
            swaggerUI("$serviceUriPrefix/openapi/api.yaml")
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

fun Application.commonConfigureStatusPages(
    block: StatusPagesConfig.() -> Unit,
) {
    install(StatusPages) {
        exception<CommonError> { call, cause ->
            val (status, message) = when (cause) {
                is ValidationError -> HttpStatusCode.BadRequest to "Validation error"
                is NotFoundError -> HttpStatusCode.NotFound to "Not found"
                is UnauthorizedError -> HttpStatusCode.Unauthorized to "Unauthorized"
                is ConflictError -> HttpStatusCode.Conflict to "Conflict"
                is BadRequestError -> HttpStatusCode.BadRequest to "Bad request"
                is ForbiddenError -> HttpStatusCode.Forbidden to "Forbidden"
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
        block()
    }
}

fun Application.commonContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = true
        })
    }
}
