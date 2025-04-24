package com.sproutscout.api.database

interface JobDao {

}

class DefaultJobDao(
    private val db: JobDb
) : JobDao {

}
