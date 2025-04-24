package com.sproutscout.api.di

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
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
import com.sproutscout.api.service.DefaultJobService
import com.sproutscout.api.service.DefaultJwtService
import com.sproutscout.api.service.EmailService
import com.sproutscout.api.service.EmailTemplateRenderer
import com.sproutscout.api.service.JobService
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
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
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

}

fun databaseModule(application: Application) = module {
    single<Jdbi> {
        application.createHikariDataSource()
            .runMigrations()
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
            developmentMode = application.developmentMode,
        )
    }

    single<AuthService> {
        DefaultAuthService(
            httpClient = get(),
            jwtService = get(),
            userDao = get(),
            refreshTokenDao = get(),
        )
    }

    single<JobService> {
        DefaultJobService(
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
        modules(appModule)
        modules(databaseModule(app))
        modules(serviceModule(app))
    }
}
