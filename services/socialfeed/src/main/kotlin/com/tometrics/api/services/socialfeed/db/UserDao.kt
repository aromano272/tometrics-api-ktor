package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {
    suspend fun insert(
        id: UserId,
        name: String,
        climateZone: ClimateZone,
    ): UserId?
    suspend fun update(
        id: UserId,
        name: String?,
        climateZone: ClimateZone?,
    )
    suspend fun delete(
        id: UserId,
    )
    suspend fun getAllByIds(
        ids: Set<UserId>,
    ): List<UserEntity>
}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {

    override suspend fun insert(
        id: UserId,
        name: String,
        climateZone: ClimateZone,
    ): UserId? = withContext(Dispatchers.IO) {
        db.insert(
            id = id,
            name = name,
            climateZone = climateZone,
        )
    }
    override suspend fun update(
        id: UserId,
        name: String?,
        climateZone: ClimateZone?,
    ) = withContext(Dispatchers.IO) {
        db.update(
            id = id,
            name = name,
            climateZone = climateZone,
        )
    }
    override suspend fun delete(
        id: UserId,
    ) = withContext(Dispatchers.IO) {
        db.delete(id = id)
    }
    override suspend fun getAllByIds(ids: Set<UserId>) = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

}