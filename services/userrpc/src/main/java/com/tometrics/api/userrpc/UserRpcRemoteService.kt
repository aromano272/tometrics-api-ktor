package com.tometrics.api.userrpc

import com.tometrics.api.common.domain.models.UserId
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc

interface UserRpcService {
    suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult
}

@Rpc
interface UserRpcRemoteService : RemoteService, UserRpcService

sealed interface ValidateUsersResult {
    data object Success : ValidateUsersResult
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : ValidateUsersResult
}