package com.tometrics.api.services.socialgraph.models

import kotlinx.serialization.Serializable

@Serializable
data class SocialConnection(
    val userId: String,
    val connectedUserId: String,
    val connectionType: ConnectionType,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ConnectionType {
    FOLLOWER,
    FOLLOWING,
    FRIEND
}