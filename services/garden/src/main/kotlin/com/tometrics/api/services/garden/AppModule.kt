package com.tometrics.api.services.garden


import com.tometrics.api.services.commongrpc.services.GardenGrpcService
import com.tometrics.api.services.garden.db.*
import com.tometrics.api.services.garden.services.*
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {

    single<PlantService> {
        DefaultPlantService(
            plantDao = get()
        )
    }

    single {
        DefaultGardenGrpcService(
            service = get(),
        )
    }

    single<GardenService> {
        DefaultGardenService(
            gardenDao = get(),
            plantService = get(),
        )
    }.bind(GardenGrpcService::class)

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

