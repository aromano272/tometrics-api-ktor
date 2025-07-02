package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.GrowthHabit

enum class GrpcGrowthHabit {
    DETERMINATE,
    INDETERMINATE,
    BUSH,
    VINING,
    UPRIGHT,
    ROSETTE,
    CLUMPING,
    SPREADING,
    MOUNDING;

    fun toNetwork(): GrowthHabit = when (this) {
        DETERMINATE -> GrowthHabit.DETERMINATE
        INDETERMINATE -> GrowthHabit.INDETERMINATE
        BUSH -> GrowthHabit.BUSH
        VINING -> GrowthHabit.VINING
        UPRIGHT -> GrowthHabit.UPRIGHT
        ROSETTE -> GrowthHabit.ROSETTE
        CLUMPING -> GrowthHabit.CLUMPING
        SPREADING -> GrowthHabit.SPREADING
        MOUNDING -> GrowthHabit.MOUNDING
    }

    companion object {
        fun fromNetwork(network: GrowthHabit): GrpcGrowthHabit = when (network) {
            GrowthHabit.DETERMINATE -> DETERMINATE
            GrowthHabit.INDETERMINATE -> INDETERMINATE
            GrowthHabit.BUSH -> BUSH
            GrowthHabit.VINING -> VINING
            GrowthHabit.UPRIGHT -> UPRIGHT
            GrowthHabit.ROSETTE -> ROSETTE
            GrowthHabit.CLUMPING -> CLUMPING
            GrowthHabit.SPREADING -> SPREADING
            GrowthHabit.MOUNDING -> MOUNDING
            GrowthHabit.UNRECOGNIZED -> throw IllegalArgumentException("Unknown GrowthHabit: $network")
        }
    }
}