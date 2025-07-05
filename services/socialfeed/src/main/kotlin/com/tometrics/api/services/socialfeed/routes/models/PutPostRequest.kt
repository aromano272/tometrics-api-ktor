package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.LocationInfoId
import kotlinx.serialization.Serializable

@Serializable
data class PutPostRequest(
    val locationInfoId: LocationInfoId?,
    val images: List<ImageUrl>?,
    val text: String?,
)