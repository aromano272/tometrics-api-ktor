package com.tometrics.api.services.socialgraph.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.jackson2.Jackson2Plugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import javax.sql.DataSource

object Database {
    fun init(environment: ApplicationEnvironment): Jdbi {
        val config = environment.config.config("database")
        
        val dataSource = HikariDataSource(HikariConfig().apply {
            driverClassName = config.property("driver").getString()
            jdbcUrl = config.property("url").getString()
            username = config.property("user").getString()
            password = config.property("password").getString()
            maximumPoolSize = 10
        })
        
        migrateDatabase(dataSource)
        
        return Jdbi.create(dataSource).apply {
            installPlugin(SqlObjectPlugin())
            installPlugin(KotlinPlugin())
            installPlugin(KotlinSqlObjectPlugin())
            installPlugin(Jackson2Plugin())
        }
    }
    
    private fun migrateDatabase(dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            
        flyway.migrate()
    }
}