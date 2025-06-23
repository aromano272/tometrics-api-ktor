package com.tometrics.api.di

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.tometrics.api.db.*
import com.tometrics.api.db.di.jdbiModule
import com.tometrics.api.external.nominatim.DefaultNominatimClient
import com.tometrics.api.external.nominatim.NominatimClient
import com.tometrics.api.service.*
import com.tometrics.api.service.geolocation.*
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
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

val qualifierLoggerUnmatchedPlaces = qualifier("loggerUnmatchedPlaces")

fun appModule(application: Application) = module {
    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    single {
        HttpClient(CIO) {
            expectSuccess = true
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

    factory<Logger>(qualifierLoggerUnmatchedPlaces) {
        LoggerFactory.getLogger("UnmatchedPlaceLogger")
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

val databaseModule = module {

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

    single<GeoNameCity500Db> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(GeoNameCity500Db::class.java)
    }

    single<GeoNameCity500Dao> {
        DefaultGeoNameCity500Dao(
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

    single<UserProfileDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(UserProfileDb::class.java)
    }

    single<UserProfileDao> {
        DefaultUserProfileDao(
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
            logger = get(),
        )
    }

    single<EmailTemplateRenderer> {
        MustacheEmailTemplateRenderer(
            mustacheFactory = get()
        )
    }

    single<ReverseGeocodingService> {
        NominatimReverseGeocodingService(
            nominatimClient = get(),
            geoNameCity500Dao = get(),
            unmatchedPlacesLogger = get(qualifierLoggerUnmatchedPlaces),
        )
    }

    single<GeolocationAutocompleteService> {
        GeoNamesAutocompleteService(
            geoNameCity500Dao = get()
        )
    }

    single<GeolocationService> {
        DefaultGeolocationService(
            reverseGeocodingService = get(),
            geolocationAutocompleteService = get()
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
            userDao = get(),
            logger = get(),
        )
    }

    single<HarvestService> {
        DefaultHarvestService(
            gardenService = get(),
            harvestDao = get(),
        )
    }

    single<UserProfileService> {
        DefaultUserProfileService(
            userProfileDao = get(),
            city500Dao = get(),
        )
    }

}

val externalModule = module {

    single<NominatimClient> {
        DefaultNominatimClient(
            client = get(),
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
            externalModule,
        )
    }
}
