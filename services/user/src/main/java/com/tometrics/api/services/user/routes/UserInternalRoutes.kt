package com.tometrics.api.services.user.routes

import com.tometrics.api.services.user.services.DefaultUserRpcService
import com.tometrics.api.services.user.services.UserService
import com.tometrics.api.userrpc.UserRpcService
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.koin.ktor.ext.inject

fun Route.userRpcRoutes() {
    val userService: UserService by inject()

    rpc {
        rpcConfig {
            serialization {
                json()
            }
        }

        registerService<UserRpcService> { ctx ->
            DefaultUserRpcService(ctx, userService)
        }
    }

}