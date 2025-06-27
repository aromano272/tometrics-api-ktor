package com.tometrics.api.services.user.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.user.db.models.UserEntity
import org.jdbi.v3.sqlobject.customizer.BindList
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery

@RegisterKotlinMapper(UserEntity::class)
interface UserDb {

    @SqlQuery("SELECT * FROM users WHERE id IN (<ids>)")
    fun getAllByIds(@BindList("ids") ids: Set<UserId>): List<UserEntity>

}