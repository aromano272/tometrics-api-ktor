package com.tometrics.api.services.user.routes.models

import com.tometrics.api.common.route.models.LocationInfoDto
import kotlinx.serialization.Serializable

@Serializable
data class GetGeolocationAutocompleteResponse(
    // TODO(aromano): sort out these locationinfo, for regular user check we should only return the bare minimum
    //                but for these autocomplete requests we should return all of the data
    val locations: List<LocationInfoDto>,
)