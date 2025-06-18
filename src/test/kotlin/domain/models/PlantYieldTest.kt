package domain.models

import com.tometrics.api.domain.models.PlantYield
import com.tometrics.api.domain.models.YieldUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class PlantYieldTest {

    @Test
    fun `test conversion from kg to lbs`() {
        val plantYield = PlantYield(from = 1f, to = 2f, unit = YieldUnit.KG)
        val converted = plantYield.asDisplayImperial()

        assertEquals(2.20462f, converted.from, 0.01f)
        assertEquals(4.40924f, converted.to, 0.01f)
        assertEquals(YieldUnit.LB, converted.unit)
    }

    @Test
    fun `test conversion from g to lbs`() {
        val plantYield = PlantYield(from = 500f, to = 1000f, unit = YieldUnit.GRAMS)
        val converted = plantYield.asDisplayImperial()

        assertEquals(1.10231f, converted.from, 0.01f)
        assertEquals(2.20462f, converted.to, 0.01f)
        assertEquals(YieldUnit.LB, converted.unit)
    }

    @Test
    fun `test conversion from lbs to kg`() {
        val plantYield = PlantYield(from = 1f, to = 2f, unit = YieldUnit.LB)
        val converted = plantYield.asDisplayMetric()

        assertEquals(453.59f, converted.from, 0.01f)
        assertEquals(907.18f, converted.to, 0.01f)
        assertEquals(YieldUnit.GRAMS, converted.unit)
    }

    @Test
    fun `test conversion from oz to kg`() {
        val plantYield = PlantYield(from = 16f, to = 32f, unit = YieldUnit.OZ)
        val converted = plantYield.asDisplayMetric()

        assertEquals(453.59f, converted.from, 0.01f)
        assertEquals(907.18f, converted.to, 0.01f)
        assertEquals(YieldUnit.GRAMS, converted.unit)
    }

    @Test
    fun `test conversion to grams when less than 1kg`() {
        val plantYield = PlantYield(from = 0.5f, to = 0.8f, unit = YieldUnit.KG)
        val converted = plantYield.asDisplayMetric()

        assertEquals(500f, converted.from, 0.01f)
        assertEquals(800f, converted.to, 0.01f)
        assertEquals(YieldUnit.GRAMS, converted.unit)
    }

    @Test
    fun `test conversion to oz when less than 1_5 lbs`() {
        val plantYield = PlantYield(from = 1f, to = 1.4f, unit = YieldUnit.LB)
        val converted = plantYield.asDisplayImperial()

        assertEquals(16f, converted.from, 0.01f)
        assertEquals(22.4f, converted.to, 0.01f)
        assertEquals(YieldUnit.OZ, converted.unit)
    }
}
