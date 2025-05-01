package com.tometrics.api.db

import com.tometrics.api.db.models.UserEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking

@RegisterKotlinMapper(UserEntity::class)
interface UserDb {

    @Blocking
    @SqlUpdate(
        """
            INSERT INTO users (name, idp_google_email, idp_facebook_id, idp_facebook_email, anon) 
            VALUES (:name, :idpGoogleEmail, :idpFacebookId, :idpFacebookEmail, :anon)
        """
    )
    @GetGeneratedKeys
    fun insert(
        @Bind("name") name: String,
        @Bind("idpGoogleEmail") idpGoogleEmail: String?,
        @Bind("idpFacebookId") idpFacebookId: String?,
        @Bind("idpFacebookEmail") idpFacebookEmail: String?,
        @Bind("anon") anon: Boolean,
    ): Int

    @Blocking
    @SqlUpdate(
        """
            UPDATE users SET
            idp_google_email = COALESCE(:idpGoogleEmail, idp_google_email),
            idp_facebook_id = COALESCE(:idpFacebookId, idp_facebook_id),
            idp_facebook_email = COALESCE(:idpFacebookEmail, idp_facebook_email),
            anon = :anon 
            WHERE id = :id
            """
    )
    fun updateAnon(
        @Bind("id") id: Int,
        @Bind("idpGoogleEmail") idpGoogleEmail: String?,
        @Bind("idpFacebookId") idpFacebookId: String?,
        @Bind("idpFacebookEmail") idpFacebookEmail: String?,
        @Bind("anon") anon: Boolean,
    ): Int

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = ANY(:ids)")
    fun getAllByIds(@Bind("ids") ids: List<Int>): List<UserEntity>

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE idp_google_email = :idpGoogleEmail")
    fun findByGoogleEmail(@Bind("idpGoogleEmail") idpGoogleEmail: String): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE idp_facebook_id = :idpFacebookId")
    fun findByFacebookId(@Bind("idpFacebookId") idpFacebookId: String): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    fun findById(@Bind("id") id: Int): UserEntity?
}
