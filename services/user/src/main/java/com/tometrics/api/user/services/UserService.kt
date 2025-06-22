package com.tometrics.api.user.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.user.db.UserDao
import io.ktor.server.plugins.*

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
        if (missingIds.isNotEmpty()) throw BadRequestException("Missing user ids: $missingIds")
    }

}