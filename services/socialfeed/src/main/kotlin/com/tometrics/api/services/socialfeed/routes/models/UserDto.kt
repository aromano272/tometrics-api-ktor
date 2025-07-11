package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.socialfeed.db.models.LocationInfoEntity
import com.tometrics.api.services.socialfeed.db.models.UserEntity


fun UserEntity.toDto(
    location: LocationInfoEntity?,
) = UserDto(
    id = id,
    name = name,
    location = location?.toDto(),
    climateZone = climateZone,
    updatedAt = updatedAt.toEpochMilli(),
)
