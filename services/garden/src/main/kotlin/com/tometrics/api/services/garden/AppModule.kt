package com.tometrics.api.services.garden


import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.tometrics.api.services.garden.db.*
import com.tometrics.api.services.garden.nominatim.DefaultNominatimClient
import com.tometrics.api.services.garden.nominatim.NominatimClient
import com.tometrics.api.services.garden.services.*
import com.tometrics.api.services.garden.services.geolocation.*
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
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import org.slf4j.LoggerFactory

fun appModule(application: Application) = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    // TODO(aromano): move this to commonservice module, along with dotenv for eg.
    factory<Logger> {
        application.environment.log
    }

}

fun serviceModule(application: Application) = module {

}

val databaseModule = module {

}

