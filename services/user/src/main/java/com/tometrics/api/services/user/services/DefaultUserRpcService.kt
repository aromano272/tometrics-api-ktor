package com.tometrics.api.services.user.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.userrpc.UserRpcService
import com.tometrics.api.userrpc.ValidateUsersResult
import kotlin.coroutines.CoroutineContext

class DefaultUserRpcService(
    override val coroutineContext: CoroutineContext,
    private val userService: UserService,
) : UserRpcService {

    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult =
        userService.validateUserIds(userIds)

}