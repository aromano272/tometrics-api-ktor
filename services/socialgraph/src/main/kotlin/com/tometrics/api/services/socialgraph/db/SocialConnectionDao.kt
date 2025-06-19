package com.tometrics.api.services.socialgraph.db

import com.tometrics.api.services.socialgraph.db.models.SocialConnectionEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface SocialConnectionDao {
    @SqlUpdate("""
        INSERT INTO social_connections (user_id, connected_user_id, connection_type, created_at)
        VALUES (:userId, :connectedUserId, :connectionType, :createdAt)
    """)
    @GetGeneratedKeys
    fun insert(@BindBean connection: SocialConnectionEntity): SocialConnectionEntity

    @SqlQuery("""
        SELECT * FROM social_connections
        WHERE user_id = :userId
    """)
    fun findByUserId(@Bind("userId") userId: String): List<SocialConnectionEntity>

    @SqlQuery("""
        SELECT * FROM social_connections
        WHERE user_id = :userId AND connection_type = :connectionType
    """)
    fun findByUserIdAndConnectionType(
        @Bind("userId") userId: String,
        @Bind("connectionType") connectionType: String
    ): List<SocialConnectionEntity>
}