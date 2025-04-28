package com.sproutscout.api.database

import com.sproutscout.api.database.models.UserEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking

@RegisterKotlinMapper(UserEntity::class)
interface UserDb {

    @Blocking
    @SqlUpdate("INSERT INTO users (name, email, idp_google_email, anon) VALUES (:name, :email, :idpGoogleEmail, :anon)")
    @GetGeneratedKeys
    fun insert(
        @Bind("name") name: String,
        @Bind("email") email: String,
        @Bind("idpGoogleEmail") idpGoogleEmail: String?,
        @Bind("anon") anon: Boolean,
    ): Int

    @Blocking
    @SqlUpdate("UPDATE users SET idp_google_email = :idpGoogleEmail, anon = :anon WHERE id = :id")
    fun updateAnon(
        @Bind("id") id: Int,
        @Bind("idpGoogleEmail") idpGoogleEmail: String?,
        @Bind("anon") anon: Boolean,
    ): Int

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = ANY(:ids)")
    fun getAllByIds(@Bind("ids") ids: List<Int>): List<UserEntity>

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE idp_google_email = :idpGoogleEmail")
    fun findByGoogleEmail(@Bind("idpGoogleEmail") idpGoogleEmail: String): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    fun findById(@Bind("id") id: Int): UserEntity?
}
