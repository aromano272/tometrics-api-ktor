package com.tometrics.api.di

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.tometrics.api.db.*
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.service.*
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
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun appModule(application: Application) = module {
    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
            engine {
                https {
                    // Trust all certificates for local development with mkcert
                    trustManager = object : javax.net.ssl.X509TrustManager {
                        override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
                        override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
                        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = emptyArray()
                    }
                }
            }
        }
    }

    single<MustacheFactory> {
        DefaultMustacheFactory("templates")
    }

    factory<Logger> {
        application.environment.log
    }

}

val databaseModule = module {

    single<GardenDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(GardenDb::class.java)
    }

    single<GardenDao> {
        DefaultGardenDao(
            db = get()
        )
    }

    single<PlantDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(PlantDb::class.java)
    }

    single<PlantDao> {
        DefaultPlantDao(
            db = get()
        )
    }

    single<HarvestDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(HarvestDb::class.java)
    }

    single<HarvestDao> {
        DefaultHarvestDao(
            db = get()
        )
    }

}

fun serviceModule(application: Application) = module {

    single<PlantService> {
        DefaultPlantService(
            plantDao = get()
        )
    }

    single<GardenService> {
        DefaultGardenService(
            gardenDao = get(),
            plantService = get(),
        )
    }

    single<EmailService> {
        MailgunEmailService(
            dotenv = get(),
            httpClient = get(),
            emailTemplateRenderer = get(),
            logger = get(),
        )
    }

    single<EmailTemplateRenderer> {
        MustacheEmailTemplateRenderer(
            mustacheFactory = get()
        )
    }

    single<DesignerService> {
        DefaultDesignerService(
            plantService = get(),
        )
    }

    single<CronjobService> {
        DefaultCronjobService(
            gardenService = get(),
            emailService = get(),
            logger = get(),
        )
    }

    single<HarvestService> {
        DefaultHarvestService(
            gardenService = get(),
            harvestDao = get(),
        )
    }

}

fun Application.configureDI() {
    val app = this

    install(Koin) {
        slf4jLogger()
        modules(
            appModule(app),
            jdbiModule(
                "classpath:db/migration",
                "classpath:com/tometrics/api/db/migration",
            ),
            databaseModule,
            serviceModule(app),
        )
    }
}
