package com.sproutscout.api.database

import com.sproutscout.api.database.models.UserEntity
import com.sproutscout.api.models.IdProviderType
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking

@RegisterKotlinMapper(UserEntity::class)
interface UserDb {

    @Blocking
    @SqlUpdate("INSERT INTO users (name, email, id_provider_type, anon) VALUES (:name, :email, :idProviderType, :anon)")
    @GetGeneratedKeys
    fun insert(
        @Bind("name") name: String,
        @Bind("email") email: String,
        @Bind("idProviderType") idProviderType: IdProviderType?,
        @Bind("anon") anon: Boolean,
    ): Int

    @Blocking
    @SqlUpdate("UPDATE users SET id_provider_type = :idProviderType, anon = :anon WHERE id = :id")
    fun updateAnon(
        @Bind("id") id: Int,
        @Bind("idProviderType") idProviderType: IdProviderType?,
        @Bind("anon") anon: Boolean,
    ): Int

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = ANY(:ids)")
    fun getAllByIds(@Bind("ids") ids: List<Int>): List<UserEntity>

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE email = :email AND id_provider_type = :idProviderType")
    fun findByEmailAndIdProviderType(
        email: String,
        idProviderType: IdProviderType,
    ): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    fun findById(@Bind("id") id: Int): UserEntity?

}