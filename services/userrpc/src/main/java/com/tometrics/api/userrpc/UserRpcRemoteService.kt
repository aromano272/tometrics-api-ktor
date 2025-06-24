package com.tometrics.api.userrpc

import com.tometrics.api.common.domain.models.UserId
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc

interface TestRpcService {
    suspend fun test(): String
}

@Rpc
interface TestRpcRemoteService : RemoteService, TestRpcService {
    override suspend fun test(): String
}

interface UserRpcService {
    suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult
}

@Rpc
interface UserRpcRemoteService : RemoteService, UserRpcService {
    // NOTE(aromano): Having 2 interfaces(*RpcService and *RpcRemoteService) with the Remote explicitly
    // overriding all of the methods is necessary because we only want the remote to be annotated with @Rpc
    // otherwise it throws when trying to get the service on the client side.
    // All of these sheninigans are because getting a client is a suspending function, which means there isn't
    // an easy way of getting these into Koin, I've created a wrapper(*RpcClient) that gets injected into the Services
    // This wrapper implements *RpcService for type safety sake and also because of Koin we cannot have this client
    // inherit from krpc.RemoteService which would force it to have a coroutineContext val, again not being able to be
    // constructed with Koin
    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult
}

sealed interface ValidateUsersResult {
    data object Success : ValidateUsersResult
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : ValidateUsersResult
}