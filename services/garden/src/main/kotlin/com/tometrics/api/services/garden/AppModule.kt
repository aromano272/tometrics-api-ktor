package com.tometrics.api.services.garden


import com.tometrics.api.services.garden.db.*
import com.tometrics.api.services.garden.services.DefaultDesignerService
import com.tometrics.api.services.garden.services.DefaultGardenService
import com.tometrics.api.services.garden.services.DefaultHarvestService
import com.tometrics.api.services.garden.services.DefaultPlantService
import com.tometrics.api.services.garden.services.DesignerService
import com.tometrics.api.services.garden.services.GardenService
import com.tometrics.api.services.garden.services.HarvestService
import com.tometrics.api.services.garden.services.PlantService
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

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

}

fun serviceModule(application: Application) = module {

    single<PlantService> {
        DefaultPlantService(
            plantDao = get()
        )
    }

    single<GardenService> {
        DefaultGardenService(
            gardenDao = get(),
            plantService = get(),
        )
    }

    single<DesignerService> {
        DefaultDesignerService(
            plantService = get(),
        )
    }

    single<HarvestService> {
        DefaultHarvestService(
            gardenService = get(),
            harvestDao = get(),
        )
    }

}

val databaseModule = module {

    single<GardenDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(GardenDb::class.java)
    }

    single<GardenDao> {
        DefaultGardenDao(
            db = get()
        )
    }

    single<PlantDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(PlantDb::class.java)
    }

    single<PlantDao> {
        DefaultPlantDao(
            db = get()
        )
    }

    single<HarvestDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(HarvestDb::class.java)
    }

    single<HarvestDao> {
        DefaultHarvestDao(
            db = get()
        )
    }

}

