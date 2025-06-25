package com.tometrics.api.services.commonclient

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.userrpc.UserRpcService
import com.tometrics.api.userrpc.ValidateUsersResult
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.RemoteService
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val krpcClientQualifier = qualifier("krpcClientQualifier")

val serviceCommonClientModule = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    single(krpcClientQualifier) {
        HttpClient(CIO) {
            installKrpc {
                waitForServices = false
            }
        }
    }

    single<UserRpcClient> {
        DefaultUserRpcClient()
    }

}

// TODO(aromano): when the target service disconnect, eg. im connecting to User from SocialGraph, if User disconnects or is rebuild,
// or for some odd reason the ws rpc connection drops, this will not automatically recover, i need to find a way to make it reconnect
interface UserRpcClient {
    suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult
}

class DefaultUserRpcClient : UserRpcClient {

    private val service = RpcServiceFactory(
        host = "localhost",
        port = 8082,
    ) {
        withService<UserRpcService>()
    }

    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult =
        service.await().validateUserIds(userIds)

}

private class RpcServiceFactory<T : RemoteService>(
    private val host: String,
    private val port: Int,
    // NOTE(aromano): workaround krpc type safety, it doesn't allow us to call `withService` with a generic
    private val provider: KtorRpcClient.() -> T,
) : KoinComponent {

    // TODO(aromano): really unsure about these shenenigans, ie. im doing this because
    // getting the rpcservice must be called from a suspending function, so i cannot
    // simply register it in Koin and have it injected into my SocialGraphService, etc..
    // I could call Route.getUserRpcService from each SocialGraphRoutes, but that would require
    // me to build a SocialGraphService for every endpoint, having all of that boilerplate
    // So i've created this so i can register it in Koin and have it injected into my SocialGraphService
    private var rpcServiceDeferred: Deferred<T>? = null
    private val rpcServiceInitMutex = Mutex()
    suspend fun await(): T {
        rpcServiceDeferred?.let { return it.await() }

        val serviceDeferred = rpcServiceInitMutex.withLock {
            rpcServiceDeferred?.let { return it.await() }
            val serviceDeferred = coroutineScope {
                async { getBaseRpcService(host, port, provider) }
            }
            rpcServiceDeferred = serviceDeferred
            serviceDeferred
        }
        return serviceDeferred.await()
    }

}

private suspend fun <T : RemoteService> KoinComponent.getBaseRpcService(
    host: String,
    port: Int,
    provider: KtorRpcClient.() -> T,
): T =
    krpcClient(
        host = host,
        port = port,
    ).provider()

private suspend fun KoinComponent.krpcClient(
    host: String,
    port: Int,
    block: HttpRequestBuilder.() -> Unit = {},
): KtorRpcClient {
    val httpClient: HttpClient = get(krpcClientQualifier)
    return httpClient.rpc {
        url {
            this@url.host = host
            this@url.port = port
            this@url.encodedPath = "internal"
        }

        rpcConfig {
            serialization {
                json()
            }
        }

        block()
    }
}

