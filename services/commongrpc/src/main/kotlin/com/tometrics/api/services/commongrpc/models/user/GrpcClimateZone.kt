package com.tometrics.api.services.commongrpc.models.user

import com.tometrics.api.services.protos.ClimateZone
import com.tometrics.api.common.domain.models.ClimateZone as DomainClimateZone

// TODO(aromano): unify this and garden's ClimateZones
enum class GrpcClimateZone {
    TEMPERATE,
    MEDITERRANEAN,
    CONTINENTAL,
    TROPICAL,
    ARID;

    companion object {
        fun fromNetwork(network: ClimateZone): GrpcClimateZone? = when (network) {
            ClimateZone.CLIMATE_ZONE_TEMPERATE -> TEMPERATE
            ClimateZone.CLIMATE_ZONE_MEDITERRANEAN -> MEDITERRANEAN
            ClimateZone.CLIMATE_ZONE_CONTINENTAL -> CONTINENTAL
            ClimateZone.CLIMATE_ZONE_TROPICAL -> TROPICAL
            ClimateZone.CLIMATE_ZONE_ARID -> ARID
            ClimateZone.UNRECOGNIZED -> null
        }
    }
}

fun GrpcClimateZone.toNetwork(): ClimateZone = when (this) {
    GrpcClimateZone.TEMPERATE -> ClimateZone.CLIMATE_ZONE_TEMPERATE
    GrpcClimateZone.MEDITERRANEAN -> ClimateZone.CLIMATE_ZONE_MEDITERRANEAN
    GrpcClimateZone.CONTINENTAL -> ClimateZone.CLIMATE_ZONE_CONTINENTAL
    GrpcClimateZone.TROPICAL -> ClimateZone.CLIMATE_ZONE_TROPICAL
    GrpcClimateZone.ARID -> ClimateZone.CLIMATE_ZONE_ARID
}

fun GrpcClimateZone.toDomain(): DomainClimateZone = when (this) {
    GrpcClimateZone.TEMPERATE -> DomainClimateZone.TEMPERATE
    GrpcClimateZone.MEDITERRANEAN -> DomainClimateZone.MEDITERRANEAN
    GrpcClimateZone.CONTINENTAL -> DomainClimateZone.CONTINENTAL
    GrpcClimateZone.TROPICAL -> DomainClimateZone.TROPICAL
    GrpcClimateZone.ARID -> DomainClimateZone.ARID
}