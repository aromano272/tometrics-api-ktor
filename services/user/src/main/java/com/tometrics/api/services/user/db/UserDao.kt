package com.tometrics.api.services.user.db

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.user.db.models.UserEntity
import com.tometrics.api.services.user.domain.models.ClimateZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {

    suspend fun insert(
        name: String,
        idpGoogleEmail: String? = null,
        idpFacebookId: String? = null,
        idpFacebookEmail: String? = null,
        anon: Boolean,
        locationId: LocationInfoId? = null,
        metricUnits: Boolean = true,
        climateZone: ClimateZone? = null,
    ): Int

    suspend fun update(
        id: UserId,
        name: String? = null,
        idpGoogleEmail: String? = null,
        idpFacebookId: String? = null,
        idpFacebookEmail: String? = null,
        anon: Boolean? = null,
        locationId: LocationInfoId? = null,
        metricUnits: Boolean? = null,
        climateZone: ClimateZone? = null,
    ): Int

    suspend fun getAllByIds(ids: Set<Int>): List<UserEntity>

    suspend fun findByGoogleEmail(idpGoogleEmail: String): UserEntity?

    suspend fun findByFacebookId(idpFacebookId: String): UserEntity?

    suspend fun findById(id: Int): UserEntity?
}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {

    override suspend fun insert(
        name: String,
        idpGoogleEmail: String?,
        idpFacebookId: String?,
        idpFacebookEmail: String?,
        anon: Boolean,
        locationId: LocationInfoId?,
        metricUnits: Boolean,
        climateZone: ClimateZone?,
    ): Int = withContext(Dispatchers.IO) {
        db.insert(name, idpGoogleEmail, idpFacebookId, idpFacebookEmail, anon, locationId, metricUnits, climateZone)
    }

    override suspend fun update(
        id: UserId,
        name: String?,
        idpGoogleEmail: String?,
        idpFacebookId: String?,
        idpFacebookEmail: String?,
        anon: Boolean?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): Int = withContext(Dispatchers.IO) {
        db.update(id, name, idpGoogleEmail, idpFacebookId, idpFacebookEmail, anon, locationId, metricUnits, climateZone)
    }

    override suspend fun getAllByIds(ids: Set<Int>): List<UserEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

    override suspend fun findByGoogleEmail(idpGoogleEmail: String): UserEntity? = withContext(Dispatchers.IO) {
        db.findByGoogleEmail(idpGoogleEmail)
    }

    override suspend fun findByFacebookId(idpFacebookId: String): UserEntity? = withContext(Dispatchers.IO) {
        db.findByFacebookId(idpFacebookId)
    }

    override suspend fun findById(id: Int): UserEntity? = withContext(Dispatchers.IO) {
        db.findById(id)
    }
}
