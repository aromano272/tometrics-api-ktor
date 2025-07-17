package com.tometrics.api.services.commonservice

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.util.logging.Logger
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun appModule(application: Application) = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    single<Logger> {
        application.environment.log
    }

    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    single {
        val json: Json = get()
        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
        }
    }

    single {
        ChannelProvider(
            logger = get(),
        )
    }

    single {
        EventProducer(
            logger = get(),
            channelProvider = get(),
        )
    }

    single<JwtService> {
        DefaultJwtService(
            dotenv = get(),
        )
    }

}
