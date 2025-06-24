package com.tometrics.api.services.commonclient

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.userrpc.UserRpcRemoteService
import com.tometrics.api.userrpc.UserRpcService
import com.tometrics.api.userrpc.ValidateUsersResult
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
import org.koin.ktor.ext.get

private val krpcClientQualifier = qualifier("krpcClientQualifier")

val serviceCommonClientModule = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    single(krpcClientQualifier) {
        HttpClient(CIO) {
            installKrpc {
                waitForServices = true
            }
        }
    }

    single<UserRpcService> {
        UserRpcClient()
    }

}

private suspend fun Route.getUserRpcService(): UserRpcService =
    krpcClient(
        host = "localhost",
        port = 8082,
    ).withService<UserRpcRemoteService>()

private suspend fun KoinComponent.getUserRpcService(): UserRpcRemoteService =
    krpcClient(
        host = "localhost",
        port = 8082,
    ).withService<UserRpcRemoteService>()

class UserRpcClient : UserRpcService, KoinComponent {

    // TODO(aromano): really unsure about these shenenigans, ie. im doing this because
    // getting the rpcservice must be called from a suspending function, so i cannot
    // simply register it in Koin and have it injected into my SocialGraphService, etc..
    // I could call Route.getUserRpcService from each SocialGraphRoutes, but that would require
    // me to build a SocialGraphService for every endpoint, having all of that boilerplate
    // So i've created this so i can register it in Koin and have it injected into my SocialGraphService
    private var rpcServiceDeferred: Deferred<UserRpcRemoteService>? = null
    private val rpcServiceInitMutex = Mutex()
    private suspend fun getService(): UserRpcRemoteService {
        rpcServiceDeferred?.let { return it.await() }

        val serviceDeferred = rpcServiceInitMutex.withLock {
            rpcServiceDeferred?.let { return it.await() }
            val serviceDeferred = coroutineScope {
                async { getUserRpcService() }
            }
            rpcServiceDeferred = serviceDeferred
            serviceDeferred
        }
        return serviceDeferred.await()
    }

    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult =
        getService().validateUserIds(userIds)

}

//class UserRpcClient : UserRpcService, KoinComponent {
//    private val rpcService = async { getUserRpcClientProvider() }
//
//    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult =
//        rpcService.await().validateUserIds(userIds)
//
//}

//private suspend fun KoinComponent.getUserRpcClientProvider(): UserRpcService {
//    return krpcClient(
//        host = "localhost",
//        port = 8082,
//    ).withService()
//}

private suspend fun Route.krpcClient(
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

