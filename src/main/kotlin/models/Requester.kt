package com.sproutscout.api.models

import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingCall
import kotlinx.serialization.Serializable

@Serializable
data class Requester(
    val userId: Int,
    val username: String,
)

private fun JWTPrincipal.asRequester(): Requester = Requester(
    userId = getClaim("userId", Int::class)
        ?: throw UnauthorizedException("userId missing from token"),
    username = getClaim("username", String::class)
        ?: throw UnauthorizedException("username missing from token"),
)

fun RoutingCall.requireRequester(): Requester =  principal<JWTPrincipal>()?.asRequester()
    ?: throw UnauthorizedException("missing jwt")
fun RoutingCall.requester(): Requester? =  principal<JWTPrincipal>()?.asRequester()