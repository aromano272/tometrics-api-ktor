package com.tometrics.api.service

import com.tometrics.api.db.GeoNameCity500Dao
import com.tometrics.api.db.UserProfileDao
import com.tometrics.api.db.models.UserProfileEntity
import com.tometrics.api.domain.models.Requester
import com.tometrics.api.domain.models.UserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class UserProfileServiceTest {

    private val userProfileDao: UserProfileDao = mockk()
    private val city500Dao: GeoNameCity500Dao = mockk()
    private val userProfileService = DefaultUserProfileService(
        userProfileDao,
        city500Dao,
    )

    @Test
    fun `given no profile for user when get profile then create and return it`() = runTest {
        val requester = Requester(1)

        val entity = UserProfileEntity(
            userId = 1,
            name = null,
            locationId = null,
            metricUnits = true,
            climateZone = null,
            updatedAt = Instant.now(),
        )
        coEvery { userProfileDao.findById(1) }.returnsMany(
            null,
            entity,
        )
        coEvery { userProfileDao.upsert(1, null, null, true, null) }.returns(1)

        val actual = userProfileService.get(requester)

        coVerify { userProfileDao.upsert(1, null, null, true, null) }

        assertEquals(
            UserProfile(1, null, null, true, null,actual.updatedAt), // ignoring updatedAt checking
            actual,
        )
    }

}