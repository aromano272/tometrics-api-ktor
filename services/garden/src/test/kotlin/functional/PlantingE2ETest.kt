package functional

import com.tometrics.api.services.garden.domain.models.Planting
import com.tometrics.api.services.garden.routes.models.AddPlantingRequest
import com.tometrics.api.services.garden.routes.models.GetAllPlantingsResponse
import com.tometrics.api.services.garden.routes.models.PatchPlantingRequest
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class PlantingE2ETest : BaseE2ETest() {

    @Test
    fun `complete planting CRUD flow`() = runApp {
        val (accessToken, _) = registerAnon()

        // Create first planting
        val planting1: Planting = postAndAssert(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(plantId = 1, quantity = 5)
        )
        assertEquals(1, planting1.plant.id)
        assertEquals("Tomatoes", planting1.name)
        assertEquals(5, planting1.quantity)
        assertEquals("", planting1.diary)
        assertEquals(false, planting1.harvested)

        // Create second planting
        val planting2: Planting = postAndAssert(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(plantId = 2, quantity = 3)
        )
        assertEquals(2, planting2.plant.id)
        assertEquals("Potatoes", planting2.name)
        assertEquals(3, planting2.quantity)
        assertEquals("", planting2.diary)
        assertEquals(false, planting2.harvested)

        // Verify both plantings are listed
        val allPlantings = getAndAssert<GetAllPlantingsResponse>("/api/v1/planting/all", accessToken).plantings
        assertEquals(2, allPlantings.size)
        assertEquals(setOf(planting1, planting2), allPlantings.toSet())

        // Update first planting quantity, name, diary, and harvested status
        val updatedPlanting: Planting = patchAndAssert(
            "/api/v1/planting/${planting1.id}",
            accessToken,
            PatchPlantingRequest(
                newQuantity = 10,
                newName = "Updated Tomatoes",
                newDiary = "Test diary entry",
                newHarvested = true
            )
        )
        assertEquals(planting1.id, updatedPlanting.id)
        assertEquals("Updated Tomatoes", updatedPlanting.name)
        assertEquals(10, updatedPlanting.quantity)
        assertEquals("Test diary entry", updatedPlanting.diary)
        assertEquals(true, updatedPlanting.harvested)

        // Delete second planting
        deleteAndAssert<Unit>(
            "/api/v1/planting/${planting2.id}",
            accessToken
        )

        // Verify final state
        val finalPlantings = getAndAssert<GetAllPlantingsResponse>("/api/v1/planting/all", accessToken).plantings
        assertEquals(1, finalPlantings.size)
        assertEquals(updatedPlanting, finalPlantings.first())
    }

    @Test
    fun `error cases for planting operations`() = runApp {
        val (accessToken, _) = registerAnon()
        val (otherUserToken, _) = registerAnon()

        // Create a planting to test with
        val planting: Planting = postAndAssert(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(plantId = 1, quantity = 5)
        )

        // Try to access with different user
        patchAndAssertFailsWith(
            "/api/v1/planting/${planting.id}",
            otherUserToken,
            PatchPlantingRequest(10),
            HttpStatusCode.Companion.BadRequest,
            null
        )

        deleteAndAssertFailsWith(
            "/api/v1/planting/${planting.id}",
            otherUserToken,
            HttpStatusCode.Companion.BadRequest,
            null
        )

        // Try to access non-existent planting
        patchAndAssertFailsWith(
            "/api/v1/planting/99999",
            accessToken,
            PatchPlantingRequest(10),
            HttpStatusCode.Companion.NotFound,
            null
        )

        deleteAndAssertFailsWith(
            "/api/v1/planting/99999",
            accessToken,
            HttpStatusCode.Companion.NotFound,
            null
        )

        // Try to create planting with invalid plant ID
        postAndAssertFailsWith(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(plantId = 99999, quantity = 5),
            HttpStatusCode.Companion.NotFound,
            null
        )

        // Try to create planting with invalid quantity
        postAndAssertFailsWith(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(plantId = 1, quantity = -1),
            HttpStatusCode.Companion.BadRequest,
            null
        )
    }

}