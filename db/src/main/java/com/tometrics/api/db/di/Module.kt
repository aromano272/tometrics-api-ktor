package com.tometrics.api.db.di

import com.tometrics.api.db.createHikariDataSource
import com.tometrics.api.db.createJdbi
import com.tometrics.api.db.runMigrations
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module
import javax.sql.DataSource

val databaseModule = module {

    single<DataSource> { (locations: List<String>) ->
        createHikariDataSource(
            dotenv = get(),
        ).runMigrations(locations)
            // TODO move out
//                "classpath:db/migration",
//                "classpath:com/tometrics/api/db/migration",
    }

    single<Jdbi> {
        val dataSource: DataSource = get()
        dataSource.createJdbi()
    }

}