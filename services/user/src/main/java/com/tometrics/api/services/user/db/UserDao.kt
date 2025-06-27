package com.tometrics.api.services.user.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.user.db.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {

    suspend fun getAllByIds(userIds: Set<UserId>): List<UserEntity>

}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {

    override suspend fun getAllByIds(userIds: Set<UserId>): List<UserEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(userIds)
    }

}