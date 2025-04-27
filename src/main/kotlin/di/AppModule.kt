package com.sproutscout.api.di

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.sproutscout.api.database.DefaultRefreshTokenDao
import com.sproutscout.api.database.DefaultUserDao
import com.sproutscout.api.database.RefreshTokenDao
import com.sproutscout.api.database.RefreshTokenDb
import com.sproutscout.api.database.UserDao
import com.sproutscout.api.database.UserDb
import com.sproutscout.api.database.createHikariDataSource
import com.sproutscout.api.database.createJdbi
import com.sproutscout.api.database.runMigrations
import com.sproutscout.api.service.AuthService
import com.sproutscout.api.service.DefaultAuthService
import com.sproutscout.api.service.DefaultGoogleAuthService
import com.sproutscout.api.service.DefaultPlantService
import com.sproutscout.api.service.DefaultJwtService
import com.sproutscout.api.service.EmailService
import com.sproutscout.api.service.EmailTemplateRenderer
import com.sproutscout.api.service.GoogleAuthService
import com.sproutscout.api.service.PlantService
import com.sproutscout.api.service.JwtService
import com.sproutscout.api.service.MailgunEmailService
import com.sproutscout.api.service.MustacheEmailTemplateRenderer
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
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

    single<RefreshTokenDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(RefreshTokenDb::class.java)
    }

    single<RefreshTokenDao> {
        DefaultRefreshTokenDao(
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
