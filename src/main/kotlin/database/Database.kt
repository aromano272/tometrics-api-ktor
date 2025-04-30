package com.sproutscout.api.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin

fun Application.createHikariDataSource(
    dotenv: Dotenv,
): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = dotenv["POSTGRES_URL"]
        username = dotenv["POSTGRES_USER"]
        password = dotenv["POSTGRES_PASSWORD"]
        maximumPoolSize = environment.config.propertyOrNull("maxPoolSize")?.getString()?.toInt() ?: 10
        isAutoCommit = true
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    return HikariDataSource(hikariConfig)
}

fun HikariDataSource.runMigrations(): HikariDataSource = also {
    val clean = true
    val flyway = Flyway.configure()
        .cleanDisabled(!clean)
        .dataSource(this)
//        .javaMigrations(
//            com.sproutscout.api.database.migrations.V2__insert_initial_plants()
//        )
        .load()

    if (clean) flyway.clean()

    flyway.migrate()
}

fun HikariDataSource.createJdbi(): Jdbi = Jdbi.create(this).apply {
    installPlugins()
    installPlugin(KotlinPlugin(enableCoroutineSupport = true))
}