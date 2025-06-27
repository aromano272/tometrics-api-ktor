package com.tometrics.api.services.user.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.user.db.UserDao
import io.ktor.util.logging.*

interface UserService {

    suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult

}

class DefaultUserService(
    private val logger: Logger,
    private val dao: UserDao,
) : UserService {

    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult {
        logger.info("validateUserIds(userIds: {})", userIds)
        val all = dao.getAllByIds(userIds)
        val foundIds = all.map { it.id }.toSet()
        val missingIds = userIds - foundIds
        if (missingIds.isNotEmpty()) return ValidateUsersResult.UserIdsNotFound(missingIds)
        return ValidateUsersResult.Success
    }

}

// TODO(aromano): probably migrate all service(data layer)-level methods to Outcome
sealed interface ValidateUsersResult {
    data object Success : ValidateUsersResult
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : ValidateUsersResult
}