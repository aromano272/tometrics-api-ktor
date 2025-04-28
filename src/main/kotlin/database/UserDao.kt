package com.sproutscout.api.database

import com.sproutscout.api.database.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {

    suspend fun insert(
        name: String,
        email: String,
        idpGoogleEmail: String?,
        anon: Boolean,
    ): Int

    suspend fun updateAnon(
        id: Int,
        idpGoogleEmail: String?,
        anon: Boolean,
    ): Int

    suspend fun getAllByIds(ids: List<Int>): List<UserEntity>

    suspend fun findByGoogleEmail(idpGoogleEmail: String): UserEntity?

    suspend fun findById(id: Int): UserEntity?
}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {
    override suspend fun insert(
        name: String,
        email: String,
        idpGoogleEmail: String?,
        anon: Boolean,
    ): Int = withContext(Dispatchers.IO) {
        db.insert(name, email, idpGoogleEmail, anon)
    }

    override suspend fun updateAnon(
        id: Int,
        idpGoogleEmail: String?,
        anon: Boolean,
    ): Int = withContext(Dispatchers.IO) {
        db.updateAnon(id, idpGoogleEmail, anon)
    }

    override suspend fun getAllByIds(ids: List<Int>): List<UserEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

    override suspend fun findByGoogleEmail(idpGoogleEmail: String): UserEntity? = withContext(Dispatchers.IO) {
        db.findByGoogleEmail(idpGoogleEmail)
    }

    override suspend fun findById(id: Int): UserEntity? = withContext(Dispatchers.IO) {
        db.findById(id)
    }
}
