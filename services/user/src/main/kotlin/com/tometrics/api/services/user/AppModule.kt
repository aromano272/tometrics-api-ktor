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
import io.ktor.util.logging.*
import org.jdbi.v3.core.Jdbi
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import org.slf4j.LoggerFactory

val qualifierLoggerUnmatchedPlaces = qualifier("loggerUnmatchedPlaces")

val appModule = module {

    factory<Logger>(qualifierLoggerUnmatchedPlaces) {
        LoggerFactory.getLogger("UnmatchedPlaceLogger")
    }

}

val serviceModule = module {

    single<UserService> {
        DefaultUserService(
            logger = get(),
            userDao = get(),
            city500Dao = get(),
            socialGraphGrpcClient = get(),
        )
    }

    single<DefaultUserGrpcService> {
        DefaultUserGrpcService(
            geolocationService = get(),
            userService = get(),
        )
    }

    single<JwtService> {
        DefaultJwtService(
            dotenv = get(),
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

    factory<GoogleIdTokenVerifier> {
        val dotenv: Dotenv = get()
        val transport = ApacheHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(dotenv["GOOGLE_OAUTH_CLIENT_ID"]))
            .build()
    }

}

