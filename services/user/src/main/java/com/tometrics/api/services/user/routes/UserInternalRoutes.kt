package com.tometrics.api.services.user.routes

import com.tometrics.api.services.user.services.DefaultTestRpcService
import com.tometrics.api.services.user.services.DefaultUserRpcRemoteService
import com.tometrics.api.services.user.services.UserService
import com.tometrics.api.userrpc.TestRpcRemoteService
import com.tometrics.api.userrpc.UserRpcRemoteService
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

        registerService<TestRpcRemoteService> { ctx ->
            DefaultTestRpcService(ctx)
        }
        registerService<UserRpcRemoteService> { ctx ->
            DefaultUserRpcRemoteService(ctx, userService)
        }
    }

//    post("/validate-users") {
//        val request = call.receive<ValidateUsersRequest>()
//        userService.validateUserIds(request.userIds.toSet())
//        call.respond(HttpStatusCode.OK)
//    }
}
