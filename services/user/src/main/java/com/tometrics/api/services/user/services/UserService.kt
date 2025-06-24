package com.tometrics.api.services.user.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.user.db.UserDao
import com.tometrics.api.services.user.domain.models.UserIdsNotFoundError

interface UserService {

    suspend fun validateUserIds(userIds: Set<UserId>)

}

class DefaultUserService(
    private val dao: UserDao,
) : UserService {

    override suspend fun validateUserIds(userIds: Set<UserId>) {
        val all = dao.getAllByIds(userIds)
        val foundIds = all.map { it.id }.toSet()
        val missingIds = userIds - foundIds
        if (missingIds.isNotEmpty()) throw UserIdsNotFoundError(missingIds)
    }

}