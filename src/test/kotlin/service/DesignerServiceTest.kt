package com.tometrics.api.service

import com.tometrics.api.domain.models.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DesignerServiceTest {
    private val plantService: PlantService = mockk()
    private val designerService: DesignerService = DefaultDesignerService(plantService)

    private val testPlant1 = Plant(
        id = 1,
        name = "Tomato",
        timeToHarvest = 90,
        yieldPerPlant = PlantYield(0.5f, 1.0f, YieldUnit.KG),
        yieldPerSqM = PlantYield(2.0f, 4.0f, YieldUnit.KG),
        companionPlants = listOf("Basil"),
        climateZones = ClimateZones(
            temperate = listOf(5, 6),
            mediterranean = listOf(4, 5),
            continental = listOf(5),
            tropical = listOf(0),
            arid = emptyList()
        ),
        spacing = SpacingRequirement.MODERATE,
        sunlight = SunlightRequirement.FULL_SUN,
        dailySunlight = DailySunlightRequirement.HIGH,
        soilType = listOf(SoilType.LOAMY),
        waterRequirement = WaterRequirement.MEDIUM,
        growthHabit = GrowthHabit.INDETERMINATE,
        growingTips = listOf(GrowingTip.PRUNE_SUCKERS)
    )

    private val testPlant2 = testPlant1.copy(
        id = 2,
        name = "Basil",
        spacing = SpacingRequirement.CLOSE
    )

    @Test
    fun `update with valid garden layout`() = runTest {
        val cells = """
            12
            1.
        """.trimIndent().toCells()
        coEvery { plantService.getAllByIds(setOf(1, 2)) } returns listOf(testPlant1, testPlant2)

        val result = designerService.update(cells)

        assertEquals(2, result.cells.size)
        assertEquals(2, result.cells[0].size)
        assertEquals(testPlant1, result.cells[0][0].plant)
        assertEquals(testPlant1, result.cells[0][1].plant)
        assertEquals(testPlant2, result.cells[1][0].plant)
        assertEquals(null, result.cells[1][1].plant)
    }

    @Test
    fun `update with invalid cell ordering throws BadRequestException`() = runTest {
        val cells = listOf(
            listOf(
                GardenCellRef(0, 1, 1), // Wrong y coordinate
                GardenCellRef(0, 1, 1)
            )
        )

        assertFailsWith<BadRequestException> {
            designerService.update(cells)
        }
    }

    @Test
    fun `update with non-existent plant ID throws NotFoundException`() = runTest {
        val cells = listOf(
            listOf(
                GardenCellRef(0, 0, 999), // Non-existent plant ID
                GardenCellRef(0, 1, null)
            )
        )
        coEvery { plantService.getAllByIds(setOf(999)) } returns emptyList()

        assertFailsWith<NotFoundException> {
            designerService.update(cells)
        }
    }

    @Test
    fun `update with empty garden`() = runTest {
        val cells = listOf<List<GardenCellRef>>()
        coEvery { plantService.getAllByIds(setOf()) } returns emptyList()

        val result = designerService.update(cells)

        assertEquals(0, result.cells.size)
        assertEquals(0, result.plantedContiguous.size)
        assertEquals(0, result.summary.size)
    }

    @Test
    fun `update correctly identifies contiguous plants`() = runTest {
        val cells = """
            11
            1.
            22
        """.trimIndent().toCells()
        coEvery { plantService.getAllByIds(setOf(1, 2)) } returns listOf(testPlant1, testPlant2)

        val result = designerService.update(cells)

        assertEquals(2, result.plantedContiguous.size)
        val plant1Group = result.plantedContiguous.find { it.first().plant?.id == 1 }
        val plant2Group = result.plantedContiguous.find { it.first().plant?.id == 2 }
        assertEquals(3, plant1Group?.size)
        assertEquals(2, plant2Group?.size)
    }

    @Test
    fun `update with large 20x20 garden layout`() = runTest {
        val cells = """
            11111.22222.33333...
            11111.22222.33333...
            11111.22222.33333...
            11111.22222.33333...
            11111.22222.33333...
            ....................
            22222.33333.11111...
            22222.33333.11111...
            22222.33333.11111...
            22222.33333.11111...
            22222.33333.11111...
            ....................
            33333.11111.22222...
            33333.11111.22222...
            33333.11111.22222...
            33333.11111.22222...
            33333.11111.22222...
            ....................
            ....................
            ....................
        """.trimIndent().toCells()

        val testPlant3 = testPlant1.copy(
            id = 3,
            name = "Pepper",
            spacing = SpacingRequirement.CLOSE
        )

        coEvery { plantService.getAllByIds(setOf(1, 2, 3)) } returns listOf(testPlant1, testPlant2, testPlant3)

        val result = designerService.update(cells)

        // Verify dimensions
        assertEquals(20, result.cells.size)
        assertEquals(20, result.cells[0].size)

        // Verify contiguous groups (should have 9 groups, 3 for each plant type)
        assertEquals(9, result.plantedContiguous.size)

        // Each plant type should have 3 groups of 25 cells each
        val groupsByPlant = result.plantedContiguous.groupBy { it.first().plant?.id }
        assertEquals(3, groupsByPlant.size)

        groupsByPlant.forEach { (_, groups) ->
            assertEquals(3, groups.size)
            groups.forEach { group ->
                assertEquals(25, group.size)
            }
        }

        // Verify summary
        assertEquals(3, result.summary.size)
        result.summary.forEach { yield ->
            // Each plant type occupies 75 cells total (3 groups of 25)
            assertEquals(yield.plant.quantityPerAreaSqM(75).toInt(), yield.quantity)
        }
    }

    private fun String.toCells() = split("\n").mapIndexed { y, row ->
        row.mapIndexed { x, ch ->
            GardenCellRef(x, y, ch.takeUnless { it == '.' }?.digitToIntOrNull())
        }
    }.transposed()

    private fun <T> List<List<T>>.transposed(): List<List<T>> {
        if (this.isEmpty() || this.first().isEmpty()) return emptyList()
        val rows = this.size
        val cols = this.first().size
        return List(cols) { col -> List(rows) { row -> this[row][col] } }
    }

}
