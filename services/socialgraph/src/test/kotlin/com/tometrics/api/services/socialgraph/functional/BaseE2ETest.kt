package com.tometrics.api.services.socialgraph.functional

import com.auth0.jwt.JWT
import com.tometrics.api.auth.domain.models.Tokens
import com.tometrics.api.common.domain.models.UserId
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.flywaydb.core.Flyway
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

interface TestUtilMethods {
    suspend fun registerAnon(): Pair<UserId, Tokens>
}

@OptIn(ExperimentalAtomicApi::class)
abstract class BaseE2ETest : KoinTest, TestUtilMethods {

    private val dataSource: HikariDataSource = run {
        val config = HikariConfig().apply {
            jdbcUrl = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
            driverClassName = postgres.driverClassName
        }
        HikariDataSource(config)
    }

    private val testDbModule = module {
        single<DataSource> {
            dataSource
        }
    }

    lateinit var testClient: HttpClient
    val realClient: HttpClient by lazy {
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
        }
    }


    fun runApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment {
            config = ApplicationConfig("application.yaml")
        }

        application {
            loadKoinModules(testDbModule)
        }

        this@BaseE2ETest.testClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        block()
    }

    override suspend fun registerAnon(): Pair<UserId, Tokens> {
        val response = realClient.post("https://localhost/api/v1/auth/anon/register")
        assertEquals(HttpStatusCode.OK, response.status)
        val tokens = response.body<Tokens>()
        val userId = JWT.decode(tokens.access).getClaim("userId").asInt()
        return userId to tokens
    }

    @BeforeTest
    fun setup() {
        // Migrate DB (reset schema if needed)
        Flyway.configure()
            .dataSource(dataSource)
            .cleanDisabled(false)
            .locations(
                "classpath:db/migration",
                "classpath:com/tometrics/api/db/migration",
            )
            .load()
            .run {
                clean()
                migrate()
            }
    }

    suspend inline fun <reified Res> getAndAssert(
        url: String,
        accessToken: String,
    ): Res = testClient.get(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <Req, reified Res> postAndAssert(
        url: String,
        accessToken: String,
        request: Req?
    ): Res = testClient.post(url) {
        bearerAuth(accessToken)
        request?.let {
            setBody(request)
            contentType(ContentType.Application.Json)
        }
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <reified Res> deleteAndAssert(
        url: String,
        accessToken: String
    ): Res = testClient.delete(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <reified Res> putAndAssert(
        url: String,
        accessToken: String
    ): Res = testClient.put(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <Req, reified Res> patchAndAssert(
        url: String,
        accessToken: String,
        request: Req?,
    ): Res = testClient.patch(url) {
        bearerAuth(accessToken)
        request?.let {
            setBody(request)
            contentType(ContentType.Application.Json)
        }
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun getAndAssertFailsWith(
        url: String,
        accessToken: String,
        code: HttpStatusCode,
        errorMessage: String?,
    ): Unit = testClient.get(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(code, status)
        errorMessage?.let { assertContains(it, bodyAsText()) }
    }

    suspend inline fun <Req> postAndAssertFailsWith(
        url: String,
        accessToken: String,
        request: Req?,
        code: HttpStatusCode,
        errorMessage: String?,
    ): Unit = testClient.post(url) {
        bearerAuth(accessToken)
        request?.let {
            setBody(request)
            contentType(ContentType.Application.Json)
        }
    }.run {
        assertEquals(code, status)
        errorMessage?.let { assertContains(it, bodyAsText()) }
    }

    suspend inline fun deleteAndAssertFailsWith(
        url: String,
        accessToken: String,
        code: HttpStatusCode,
        errorMessage: String?,
    ): Unit = testClient.delete(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(code, status)
        errorMessage?.let { assertContains(it, bodyAsText()) }
    }

    suspend inline fun putAndAssertFailsWith(
        url: String,
        accessToken: String,
        code: HttpStatusCode,
        errorMessage: String?,
    ): Unit = testClient.put(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(code, status)
        errorMessage?.let { assertContains(it, bodyAsText()) }
    }

    suspend inline fun <Req> patchAndAssertFailsWith(
        url: String,
        accessToken: String,
        request: Req?,
        code: HttpStatusCode,
        errorMessage: String?,
    ): Unit = testClient.patch(url) {
        bearerAuth(accessToken)
        request?.let {
            setBody(request)
            contentType(ContentType.Application.Json)
        }
    }.run {
        assertEquals(code, status)
        errorMessage?.let { assertContains(it, bodyAsText()) }
    }

    companion object {
        // Start Postgres once per class
        @JvmStatic
        protected val postgres = PostgreSQLContainer("postgres:14").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            start()
        }
    }
}