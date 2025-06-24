package com.tometrics.api.services.user.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.userrpc.UserRpcRemoteService
import com.tometrics.api.userrpc.ValidateUsersResult
import kotlin.coroutines.CoroutineContext

class DefaultUserRpcRemoteService(
    override val coroutineContext: CoroutineContext,
    private val userService: UserService,
) : UserRpcRemoteService {

    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult =
        userService.validateUserIds(userIds)

}