package com.tometrics.api.services.user


import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.tometrics.api.services.user.db.*
import com.tometrics.api.services.user.nominatim.DefaultNominatimClient
import com.tometrics.api.services.user.nominatim.NominatimClient
import com.tometrics.api.services.user.services.*
import com.tometrics.api.services.user.services.geolocation.*
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

val qualifierLoggerUnmatchedPlaces = qualifier("loggerUnmatchedPlaces")

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

    // TODO(aromano): move to commonservices or something
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
            // TODO(aromano): probably no longer needed
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

}

fun serviceModule(application: Application) = module {

    single<UserService> {
        DefaultUserService(
            logger = get(),
            userDao = get(),
            city500Dao = get(),
        )
    }

    single<UserGrpcService> {
        UserGrpcService(
            userService = get(),
        )
    }

    single<JwtService> {
        DefaultJwtService(
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

    single<RefreshTokenDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(RefreshTokenDb::class.java)
    }

    single<RefreshTokenDao> {
        DefaultRefreshTokenDao(
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

}

val externalModule = module {

    single<NominatimClient> {
        DefaultNominatimClient(
            client = get(),
        )
    }

}

