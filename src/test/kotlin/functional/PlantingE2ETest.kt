package com.tometrics.api.functional

import com.tometrics.api.domain.models.Planting
import com.tometrics.api.routes.models.AddPlantingRequest
import com.tometrics.api.routes.models.GetAllPlantingsResponse
import com.tometrics.api.routes.models.PatchPlantingRequest
import io.ktor.http.*
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
            AddPlantingRequest(1, 5)
        )
        assertEquals(1, planting1.plant.id)
        assertEquals(5, planting1.quantity)

        // Create second planting
        val planting2: Planting = postAndAssert(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(2, 3)
        )
        assertEquals(2, planting2.plant.id)
        assertEquals(3, planting2.quantity)

        // Verify both plantings are listed
        val allPlantings = getAndAssert<GetAllPlantingsResponse>("/api/v1/planting/all", accessToken).plantings
        assertEquals(2, allPlantings.size)
        assertEquals(setOf(planting1, planting2), allPlantings.toSet())

        // Update first planting quantity
        val updatedPlanting: Planting = patchAndAssert(
            "/api/v1/planting/${planting1.id}",
            accessToken,
            PatchPlantingRequest(10)
        )
        assertEquals(planting1.id, updatedPlanting.id)
        assertEquals(10, updatedPlanting.quantity)

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
            AddPlantingRequest(1, 5)
        )

        // Try to access with different user
        patchAndAssertFailsWith(
            "/api/v1/planting/${planting.id}",
            otherUserToken,
            PatchPlantingRequest(10),
            HttpStatusCode.BadRequest,
            null
        )

        deleteAndAssertFailsWith(
            "/api/v1/planting/${planting.id}",
            otherUserToken,
            HttpStatusCode.BadRequest,
            null
        )

        // Try to access non-existent planting
        patchAndAssertFailsWith(
            "/api/v1/planting/99999",
            accessToken,
            PatchPlantingRequest(10),
            HttpStatusCode.NotFound,
            null
        )

        deleteAndAssertFailsWith(
            "/api/v1/planting/99999",
            accessToken,
            HttpStatusCode.NotFound,
            null
        )

        // Try to create planting with invalid plant ID
        postAndAssertFailsWith(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(99999, 5),
            HttpStatusCode.NotFound,
            null
        )

        // Try to create planting with invalid quantity
        postAndAssertFailsWith(
            "/api/v1/planting/add",
            accessToken,
            AddPlantingRequest(1, -1),
            HttpStatusCode.BadRequest,
            null
        )
    }

}
