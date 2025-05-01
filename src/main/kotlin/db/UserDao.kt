package com.tometrics.api.db

import com.tometrics.api.db.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {

    suspend fun insert(
        name: String,
        idpGoogleEmail: String? = null,
        idpFacebookId: String? = null,
        idpFacebookEmail: String? = null,
        anon: Boolean,
    ): Int

    suspend fun updateAnon(
        id: Int,
        idpGoogleEmail: String? = null,
        idpFacebookId: String? = null,
        idpFacebookEmail: String? = null,
        anon: Boolean,
    ): Int

    suspend fun getAllByIds(ids: List<Int>): List<UserEntity>

    suspend fun findByGoogleEmail(idpGoogleEmail: String): UserEntity?

    suspend fun findByFacebookId(idpFacebookId: String): UserEntity?

    suspend fun findById(id: Int): UserEntity?
}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {
    override suspend fun insert(
        name: String,
        idpGoogleEmail: String?,
        idpFacebookId: String?,
        idpFacebookEmail: String?,
        anon: Boolean,
    ): Int = withContext(Dispatchers.IO) {
        db.insert(name, idpGoogleEmail, idpFacebookId, idpFacebookEmail, anon)
    }

    override suspend fun updateAnon(
        id: Int,
        idpGoogleEmail: String?,
        idpFacebookId: String?,
        idpFacebookEmail: String?,
        anon: Boolean,
    ): Int = withContext(Dispatchers.IO) {
        db.updateAnon(id, idpGoogleEmail, idpFacebookId, idpFacebookEmail, anon)
    }

    override suspend fun getAllByIds(ids: List<Int>): List<UserEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

    override suspend fun findByGoogleEmail(idpGoogleEmail: String): UserEntity? = withContext(Dispatchers.IO) {
        db.findByGoogleEmail(idpGoogleEmail)
    }

    override suspend fun findByFacebookId(idpFacebookId: String): UserEntity? = withContext(Dispatchers.IO) {
        db.findByFacebookId(idpFacebookId)
    }

    override suspend fun findById(id: Int): UserEntity? = withContext(Dispatchers.IO) {
        db.findById(id)
    }
}
