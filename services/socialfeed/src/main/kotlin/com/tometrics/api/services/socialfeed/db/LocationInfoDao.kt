package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.LocationInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface LocationInfoDao {
    suspend fun insert(
        locationId: LocationInfoId,
        city: String?,
        country: String?,
        countryCode: String?,
    ): UserId?
    suspend fun update(
        locationId: LocationInfoId,
        city: String?,
        country: String?,
        countryCode: String?,
    )
    suspend fun delete(locationId: LocationInfoId)
    suspend fun findById(id: LocationInfoId): LocationInfoEntity?
    suspend fun getAllByIds(ids: Set<LocationInfoId>): List<LocationInfoEntity>
}

class DefaultLocationInfoDao(
    private val db: LocationInfoDb,
) : LocationInfoDao {

    override suspend fun insert(
        locationId: LocationInfoId,
        city: String?,
        country: String?,
        countryCode: String?,
    ): UserId? = withContext(Dispatchers.IO) {
        db.insert(
            locationId = locationId,
            city = city,
            country = country,
            countryCode = countryCode,
        )
    }
    override suspend fun update(
        locationId: LocationInfoId,
        city: String?,
        country: String?,
        countryCode: String?,
    ) = withContext(Dispatchers.IO) {
        db.update(
            locationId = locationId,
            city = city,
            country = country,
            countryCode = countryCode,
        )
    }
    override suspend fun delete(
        locationId: LocationInfoId,
    ) = withContext(Dispatchers.IO) {
        db.delete(locationId = locationId)
    }

    override suspend fun findById(id: LocationInfoId): LocationInfoEntity? = withContext(Dispatchers.IO) {
        db.findById(id)
    }

    override suspend fun getAllByIds(ids: Set<LocationInfoId>): List<LocationInfoEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

}