package com.tometrics.api.services.user.db

import com.tometrics.api.services.user.db.models.GeoNameCity500Entity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GeoNameCity500Dao {
    suspend fun getAll(): List<GeoNameCity500Entity>
    suspend fun getById(id: Int): GeoNameCity500Entity?
    suspend fun getByName(name: String): List<GeoNameCity500Entity>
    suspend fun getByCountryCode(countryCode: String): List<GeoNameCity500Entity>
    suspend fun findByNameAndAdmin1Similarity(
        name: String,
        countryCode: String,
        asciiadmin1: String?,
    ): GeoNameCity500Entity?
    suspend fun search(query: String): List<GeoNameCity500Entity>
}

class DefaultGeoNameCity500Dao(
    private val db: GeoNameCity500Db
) : GeoNameCity500Dao {

    override suspend fun getAll(): List<GeoNameCity500Entity> = withContext(Dispatchers.IO) {
        db.getAll()
    }

    override suspend fun getById(id: Int): GeoNameCity500Entity? = withContext(Dispatchers.IO) {
        db.getById(id)
    }

    override suspend fun getByName(name: String): List<GeoNameCity500Entity> = withContext(Dispatchers.IO) {
        db.getByName(name)
    }

    override suspend fun getByCountryCode(countryCode: String): List<GeoNameCity500Entity> = withContext(Dispatchers.IO) {
        db.getByCountryCode(countryCode)
    }

    override suspend fun findByNameAndAdmin1Similarity(
        name: String,
        countryCode: String,
        asciiadmin1: String?,
    ): GeoNameCity500Entity? = withContext(Dispatchers.IO) {
        db.findByNameAndAdmin1Similarity(name, countryCode, asciiadmin1)
    }

    override suspend fun search(query: String): List<GeoNameCity500Entity> = withContext(Dispatchers.IO) {
        db.search(query)
    }
}
