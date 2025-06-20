package com.tometrics.api.db

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.flywaydb.core.Flyway
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.jackson2.Jackson2Config
import org.jdbi.v3.jackson2.Jackson2Plugin
import javax.sql.DataSource

fun createHikariDataSource(
    dotenv: Dotenv,
): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = dotenv["POSTGRES_URL"]
        username = dotenv["POSTGRES_USER"]
        password = dotenv["POSTGRES_PASSWORD"]
        maximumPoolSize = dotenv["POSTGRES_MAX_POOL_SIZE"].toIntOrNull() ?: 10
        isAutoCommit = true
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    return HikariDataSource(hikariConfig)
}

fun HikariDataSource.runMigrations(vararg locations: String): HikariDataSource = also {
    val clean = false
    val flyway = Flyway.configure()
        .cleanDisabled(!clean)
        .dataSource(this)
        .locations(*locations)
        .load()

    if (clean) flyway.clean()

    flyway.migrate()
}

fun DataSource.createJdbi(): Jdbi = Jdbi.create(this).apply {
    installPlugins()
    installPlugin(KotlinPlugin(enableCoroutineSupport = true))
    installPlugin(Jackson2Plugin())
    getConfig(Jackson2Config::class.java).mapper = jacksonObjectMapper()
}