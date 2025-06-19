package com.tometrics.api.services.socialgraph.db.models

import com.tometrics.api.services.socialgraph.models.ConnectionType
import com.tometrics.api.services.socialgraph.models.SocialConnection

data class SocialConnectionEntity(
    val id: Int? = null,
    val userId: String,
    val connectedUserId: String,
    val connectionType: String,
    val createdAt: Long
) {
    companion object {
        fun fromDomain(domain: SocialConnection): SocialConnectionEntity {
            return SocialConnectionEntity(
                id = null,
                userId = domain.userId,
                connectedUserId = domain.connectedUserId,
                connectionType = domain.connectionType.name,
                createdAt = domain.createdAt
            )
        }
    }

    fun toDomain(): SocialConnection {
        return SocialConnection(
            userId = userId,
            connectedUserId = connectedUserId,
            connectionType = ConnectionType.valueOf(connectionType),
            createdAt = createdAt
        )
    }
}