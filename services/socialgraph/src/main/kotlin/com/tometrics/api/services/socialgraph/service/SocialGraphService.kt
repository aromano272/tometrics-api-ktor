package com.tometrics.api.services.socialgraph.service

import com.tometrics.api.services.socialgraph.db.SocialConnectionDao
import com.tometrics.api.services.socialgraph.db.models.SocialConnectionEntity
import com.tometrics.api.services.socialgraph.models.ConnectionType
import com.tometrics.api.services.socialgraph.models.SocialConnection
import org.jdbi.v3.core.Jdbi


class SocialGraphService(private val jdbi: Jdbi) {

    fun addConnection(userId: String, connectedUserId: String, connectionType: ConnectionType): SocialConnection {
        val connection = SocialConnection(userId, connectedUserId, connectionType)

        return jdbi.withExtension<SocialConnection, SocialConnectionDao, Exception>(SocialConnectionDao::class.java) { dao ->
            val entity = SocialConnectionEntity.fromDomain(connection)
            dao.insert(entity).toDomain()
        }
    }

    fun getConnections(userId: String): List<SocialConnection> {
        TODO()
    }

    fun getConnectionsByType(userId: String, connectionType: ConnectionType): List<SocialConnection> {
        TODO()
    }
}

