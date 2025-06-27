package com.tometrics.api.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.db.GardenDao
import com.tometrics.api.db.models.PlantingEntity
import com.tometrics.api.domain.models.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class GardenServiceTest {

    private val gardenDao: GardenDao = mockk()
    private val plantService: PlantService = mockk()
    private val gardenService: GardenService = DefaultGardenService(gardenDao, plantService)

    @Test
    fun `should generate correct planting name when no existing plantings`() = runTest {
        val requester = Requester(1)
        val plant = TOMATO
        val quantity = 5

        coEvery { plantService.getById(plant.id) } returns plant
        coEvery { gardenDao.getSamePlantPlantings(requester.userId, plant.id) } returns emptyList()
        coEvery { gardenDao.insert(any(), any(), any(), any(), any(), any(), any()) } returns 1
        coEvery { gardenDao.find(any()) } returns PlantingEntity(
            id = 1,
            userId = requester.userId,
            plantId = plant.id,
            name = "Tomato",
            quantity = quantity,
            createdAt = Instant.now(),
            readyToHarvestAt = Instant.now().plus(10, ChronoUnit.DAYS),
            diary = "",
            harvested = false
        )

        val result = gardenService.add(requester, plant.id, quantity)

        assertEquals("Tomato", result.name)
    }

    @Test
    fun `should generate correct planting name with date when existing plantings exist`() = runTest {
        val requester = Requester(1)
        val plant = TOMATO
        val quantity = 5
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)

        coEvery { plantService.getById(plant.id) } returns plant
        coEvery { gardenDao.getSamePlantPlantings(requester.userId, plant.id) } returns listOf(
            PlantingEntity(
                id = 1,
                userId = requester.userId,
                plantId = plant.id,
                name = "Tomato 2023-10-01",
                quantity = 3,
                createdAt = today,
                readyToHarvestAt = Instant.now().plus(10, ChronoUnit.DAYS),
                diary = "",
                harvested = false
            )
        )
        coEvery { gardenDao.insert(any(), any(), any(), any(), any(), any(), any()) } returns 2
        coEvery { gardenDao.find(any()) } returns PlantingEntity(
            id = 2,
            userId = requester.userId,
            plantId = plant.id,
            name = "Tomato 2023-10-01 #2",
            quantity = quantity,
            createdAt = Instant.now(),
            readyToHarvestAt = Instant.now().plus(10, ChronoUnit.DAYS),
            diary = "",
            harvested = false
        )

        val result = gardenService.add(requester, plant.id, quantity)

        assertEquals("Tomato 2023-10-01 #2", result.name)
    }

    @Test
    fun `should generate correct planting name with date when no plantings exist for today`() = runTest {
        val requester = Requester(1)
        val plant = TOMATO
        val quantity = 5
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)

        coEvery { plantService.getById(plant.id) } returns plant
        coEvery { gardenDao.getSamePlantPlantings(requester.userId, plant.id) } returns listOf(
            PlantingEntity(
                id = 1,
                userId = requester.userId,
                plantId = plant.id,
                name = "Tomato 2023-09-30",
                quantity = 3,
                createdAt = today.minus(1, ChronoUnit.DAYS),
                readyToHarvestAt = Instant.now().plus(10, ChronoUnit.DAYS),
                diary = "",
                harvested = false
            )
        )
        coEvery { gardenDao.insert(any(), any(), any(), any(), any(), any(), any()) } returns 2
        coEvery { gardenDao.find(any()) } returns PlantingEntity(
            id = 2,
            userId = requester.userId,
            plantId = plant.id,
            name = "Tomato 2023-10-01",
            quantity = quantity,
            createdAt = Instant.now(),
            readyToHarvestAt = Instant.now().plus(10, ChronoUnit.DAYS),
            diary = "",
            harvested = false
        )

        val result = gardenService.add(requester, plant.id, quantity)

        assertEquals("Tomato 2023-10-01", result.name)
    }

    companion object {
        private val TOMATO = Plant(1, "Tomato", 10, PlantYield(1f, 2f, YieldUnit.KG), PlantYield(10f, 20f, YieldUnit.KG), emptyList(), ClimateZones(emptyList(), emptyList(), emptyList(), emptyList(), emptyList()), SpacingRequirement.CLOSE, SunlightRequirement.FULL_SUN, DailySunlightRequirement.HIGH, emptyList(), WaterRequirement.MEDIUM, GrowthHabit.BUSH, emptyList())
    }

}
