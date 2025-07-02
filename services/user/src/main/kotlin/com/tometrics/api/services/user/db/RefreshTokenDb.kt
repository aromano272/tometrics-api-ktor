package com.tometrics.api.services.user.db

import com.tometrics.api.services.user.db.models.RefreshTokenEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking
import java.time.Instant

@RegisterKotlinMapper(RefreshTokenEntity::class)
interface RefreshTokenDb {

    @Blocking
    @SqlUpdate(
        """
        INSERT INTO refresh_tokens (user_id, token, expires_at)
        VALUES (:userId, :token, :expiresAt)
    """
    )
    fun insert(
        @Bind("userId") userId: Int,
        @Bind("token") token: String,
        @Bind("expiresAt") expiresAt: Instant,
    )

    @Blocking
    @SqlQuery("SELECT * FROM refresh_tokens WHERE token = :token")
    fun findByToken(@Bind("token") token: String): RefreshTokenEntity?

    @Blocking
    @SqlUpdate("DELETE FROM refresh_tokens WHERE token = :token")
    fun delete(@Bind("token") token: String)

    @Blocking
    @SqlUpdate("DELETE FROM refresh_tokens WHERE user_id = :userId")
    fun deleteAllByUser(@Bind("userId") userId: Int)

    @Blocking
    @SqlUpdate("DELETE FROM refresh_tokens WHERE expires_at < now()")
    fun cleanExpired()
}
