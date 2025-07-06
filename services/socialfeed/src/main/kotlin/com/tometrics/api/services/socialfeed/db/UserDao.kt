package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {
    suspend fun insert(
        id: UserId,
        name: String,
        locationId: LocationInfoId,
        climateZone: ClimateZone,
    ): UserId?
    suspend fun update(
        id: UserId,
        name: String?,
        locationId: LocationInfoId?,
        climateZone: ClimateZone?,
    )
    suspend fun delete(
        id: UserId,
    )
}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {

    override suspend fun insert(
        id: UserId,
        name: String,
        locationId: LocationInfoId,
        climateZone: ClimateZone,
    ): UserId? = withContext(Dispatchers.IO) {
        db.insert(
            id = id,
            name = name,
            locationId = locationId,
            climateZone = climateZone,
        )
    }
    override suspend fun update(
        id: UserId,
        name: String?,
        locationId: LocationInfoId?,
        climateZone: ClimateZone?,
    ) = withContext(Dispatchers.IO) {
        db.update(
            id = id,
            name = name,
            locationId = locationId,
            climateZone = climateZone,
        )
    }
    override suspend fun delete(
        id: UserId,
    ) = withContext(Dispatchers.IO) {
        db.delete(id = id)
    }

}