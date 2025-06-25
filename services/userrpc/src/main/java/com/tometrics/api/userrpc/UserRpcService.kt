package com.tometrics.api.userrpc

import com.tometrics.api.common.domain.models.UserId
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable

@Rpc
interface UserRpcService : RemoteService {
    suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult
}

@Serializable
sealed interface ValidateUsersResult {
    @Serializable
    data object Success : ValidateUsersResult
    @Serializable
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : ValidateUsersResult
}