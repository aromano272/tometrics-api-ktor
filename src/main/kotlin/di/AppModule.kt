package com.sproutscout.api.di

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.sproutscout.api.database.*
import com.sproutscout.api.service.*
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
                json()
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
        }
    }

    single<MustacheFactory> {
        DefaultMustacheFactory("templates")
    }

    factory<Logger> {
        application.environment.log
    }

    factory<GoogleIdTokenVerifier> {
        val dotenv: Dotenv = get()
        val transport = ApacheHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(dotenv["GOOGLE_OAUTH_CLIENT_ID"]))
            .build()
    }

}

fun databaseModule(application: Application) = module {
    single<Jdbi> {
        application.createHikariDataSource(
            dotenv = get(),
        ).runMigrations()
            .createJdbi()
    }

    single<UserDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(UserDb::class.java)
    }

    single<UserDao> {
        DefaultUserDao(
            db = get()
        )
    }

    single<GardenDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(GardenDb::class.java)
    }

    single<GardenDao> {
        DefaultGardenDao(
            db = get()
        )
    }

    single<RefreshTokenDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(RefreshTokenDb::class.java)
    }

    single<RefreshTokenDao> {
        DefaultRefreshTokenDao(
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

}

fun serviceModule(application: Application) = module {

    single<JwtService> {
        DefaultJwtService(
            config = application.environment.config,
            dotenv = get(),
            developmentMode = application.developmentMode,
        )
    }

    single<AuthService> {
        DefaultAuthService(
            jwtService = get(),
            googleAuthService = get(),
            userDao = get(),
            refreshTokenDao = get(),
        )
    }

    single<GoogleAuthService> {
        DefaultGoogleAuthService(
            verifier = get(),
        )
    }

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
            logger = application.environment.log,
        )
    }

    single<EmailTemplateRenderer> {
        MustacheEmailTemplateRenderer(
            mustacheFactory = get()
        )
    }

    single<CronjobService> {
        DefaultCronjobService(
            gardenService = get(),
            emailService = get(),
            userDao = get(),
            logger = application.environment.log,
        )
    }

}

fun Application.configureDI() {
    val app = this

    install(Koin) {
        slf4jLogger()
        modules(appModule(app))
        modules(databaseModule(app))
        modules(serviceModule(app))
    }
}
