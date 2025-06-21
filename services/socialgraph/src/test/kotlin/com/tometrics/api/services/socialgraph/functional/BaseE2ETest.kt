package com.tometrics.api.services.socialgraph.functional

import com.tometrics.api.auth.domain.models.Tokens
import com.tometrics.api.common.domain.models.UserId
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.flywaydb.core.Flyway
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
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

    lateinit var jsonClient: HttpClient

    fun runApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment {
            config = ApplicationConfig("application.yaml")
        }

        application {
            loadKoinModules(testDbModule)
        }

        this@BaseE2ETest.jsonClient = createClient {
            install(Logging) {
                level = LogLevel.INFO
            }
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                url("https://localhost")
            }
        }

        block()
    }

    // TODO(aromano): mocking register user with hardcoded tokens for now, it was 404 on the endpoint even when i have the docker service running
    private val tokens = listOf(
        5 to Tokens(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ0b21ldHJpY3MtdXNlcnMiLCJpc3MiOiJ0b21ldHJpY3MuY29tIiwidXNlcklkIjo1LCJhbm9uIjp0cnVlLCJleHAiOjE3NTMwNTUyMjd9.SAQRvC2OmxyAUgFbU6EjyjMt1dIikWt_8tdSvr2Vq10",
            refresh =  "8da29913-9a22-440b-9302-d0789492ec78"
        ),
        6 to Tokens(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ0b21ldHJpY3MtdXNlcnMiLCJpc3MiOiJ0b21ldHJpY3MuY29tIiwidXNlcklkIjo2LCJhbm9uIjp0cnVlLCJleHAiOjE3NTMwNTUyNDh9.9r9BhJzNaxYFYs3yMxcFWZdGiy7E_Tze64f6lki3xd8",
            refresh =  "d125d1c8-8a67-424b-aa80-86984f650898"
        ),
        7 to Tokens(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ0b21ldHJpY3MtdXNlcnMiLCJpc3MiOiJ0b21ldHJpY3MuY29tIiwidXNlcklkIjo3LCJhbm9uIjp0cnVlLCJleHAiOjE3NTMwNTUyNTZ9.DIsNxg1igrFCh2C8s0t3W4aK8caypn9unuIgD-jWULg",
            refresh =  "a1561c8d-f58e-4a62-8b33-b80500758d91"
        ),
        8 to Tokens(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ0b21ldHJpY3MtdXNlcnMiLCJpc3MiOiJ0b21ldHJpY3MuY29tIiwidXNlcklkIjo4LCJhbm9uIjp0cnVlLCJleHAiOjE3NTMwNTUyNjF9.QcRvl50Ejxb6sBSMBNKZaWkOLtAlQUyAQA3ri9AArOg",
            refresh =  "dfba2bd9-669e-4988-8e4c-20bd9722f396"
        ),
        9 to Tokens(
            access = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ0b21ldHJpY3MtdXNlcnMiLCJpc3MiOiJ0b21ldHJpY3MuY29tIiwidXNlcklkIjo5LCJhbm9uIjp0cnVlLCJleHAiOjE3NTMwNTUyNjd9.-bG1a4NW6I0ovQJ3om2fCoyVjRr_WIekb2v9B93TShU",
            refresh =  "e01127c0-290d-41d3-a695-619b80f22fd6"
        ),
    )
    private val currToken = AtomicInt(0)
    override suspend fun registerAnon(): Pair<UserId, Tokens> {
//        val response = jsonClient.post("/api/v1/auth/anon/register")
//        assertEquals(HttpStatusCode.OK, response.status)
//        val tokens = response.body<Tokens>()
        val tokenIndex = currToken.fetchAndIncrement() % tokens.size
        return tokens[tokenIndex]
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
    ): Res = jsonClient.get(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <Req, reified Res> postAndAssert(
        url: String,
        accessToken: String,
        request: Req?
    ): Res = jsonClient.post(url) {
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
    ): Res = jsonClient.delete(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <reified Res> putAndAssert(
        url: String,
        accessToken: String
    ): Res = jsonClient.put(url) {
        bearerAuth(accessToken)
    }.run {
        assertEquals(HttpStatusCode.OK, status)
        assertIs<Res>(body<Res>())
    }

    suspend inline fun <Req, reified Res> patchAndAssert(
        url: String,
        accessToken: String,
        request: Req?,
    ): Res = jsonClient.patch(url) {
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
    ): Unit = jsonClient.get(url) {
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
    ): Unit = jsonClient.post(url) {
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
    ): Unit = jsonClient.delete(url) {
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
    ): Unit = jsonClient.put(url) {
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
    ): Unit = jsonClient.patch(url) {
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