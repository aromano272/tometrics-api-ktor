package com.tometrics.api.db.di

import com.tometrics.api.db.createHikariDataSource
import com.tometrics.api.db.createJdbi
import com.tometrics.api.db.runMigrations
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module
import javax.sql.DataSource

fun jdbiModule(vararg migrationLocations: String) = module {

    single<DataSource> {
        createHikariDataSource(
            dotenv = get(),
        ).runMigrations(*migrationLocations)
    }

    single<Jdbi> {
        val dataSource: DataSource = get()
        dataSource.createJdbi()
    }

}