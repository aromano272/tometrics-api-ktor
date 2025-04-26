package com.sproutscout.api.database

import com.sproutscout.api.database.models.UserEntity
import com.sproutscout.api.models.IdProviderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserDao {

    suspend fun insert(
        name: String,
        email: String,
        idProviderType: IdProviderType?,
        anon: Boolean,
    ): Int

    suspend fun updateAnon(
        id: Int,
        idProviderType: IdProviderType?,
        anon: Boolean,
    ): Int

    suspend fun getAllByIds(ids: List<Int>): List<UserEntity>

    suspend fun findByEmailAndIdProviderType(
        email: String,
        idProviderType: IdProviderType,
    ): UserEntity?

    suspend fun findById(id: Int): UserEntity?

}

class DefaultUserDao(
    private val db: UserDb,
) : UserDao {
    override suspend fun insert(
        name: String,
        email: String,
        idProviderType: IdProviderType?,
        anon: Boolean,
    ): Int = withContext(Dispatchers.IO) {
        db.insert(name, email, idProviderType, anon)
    }

    override suspend fun updateAnon(
        id: Int,
        idProviderType: IdProviderType?,
        anon: Boolean,
    ): Int = withContext(Dispatchers.IO) {
        db.updateAnon(id, idProviderType, anon)
    }

    override suspend fun getAllByIds(ids: List<Int>): List<UserEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

    override suspend fun findByEmailAndIdProviderType(
        email: String,
        idProviderType: IdProviderType,
    ): UserEntity? = withContext(Dispatchers.IO) {
        db.findByEmailAndIdProviderType(email, idProviderType)
    }

    override suspend fun findById(id: Int): UserEntity? = withContext(Dispatchers.IO) {
        db.findById(id)
    }
}