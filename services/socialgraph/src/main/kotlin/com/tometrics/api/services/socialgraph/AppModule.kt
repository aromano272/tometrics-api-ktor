package com.tometrics.api.services.socialgraph

import com.tometrics.api.services.socialgraph.db.DefaultFollowerDao
import com.tometrics.api.services.socialgraph.db.FollowerDao
import com.tometrics.api.services.socialgraph.db.FollowerDb
import com.tometrics.api.services.socialgraph.service.DefaultSocialGraphService
import com.tometrics.api.services.socialgraph.service.SocialGraphService
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

val appModule = module {

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
        }
    }

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

}

val serviceModule = module {

    factory<SocialGraphService> {
        DefaultSocialGraphService(
            userRpcService = get(),
            dao = get(),
        )
    }

}

val databaseModule = module {

    single<FollowerDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(FollowerDb::class.java)
    }

    single<FollowerDao> {
        DefaultFollowerDao(
            db = get()
        )
    }

}
