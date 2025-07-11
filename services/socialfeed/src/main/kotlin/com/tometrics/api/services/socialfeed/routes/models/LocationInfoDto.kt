package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.route.models.LocationInfoDto
import com.tometrics.api.services.commongrpc.models.user.GrpcLocationInfo
import com.tometrics.api.services.socialfeed.db.models.LocationInfoEntity

fun LocationInfoEntity.toDto() = LocationInfoDto(
    id = locationId,
    city = city,
    country = country,
    countryCode = countryCode,
)

fun GrpcLocationInfo.toDto(): LocationInfoDto = LocationInfoDto(
    id = id,
    city = city,
    country = country,
    countryCode = countryCode,
)

