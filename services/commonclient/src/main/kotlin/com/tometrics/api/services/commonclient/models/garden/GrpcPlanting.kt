package com.tometrics.api.services.commonclient.models.garden

import com.tometrics.api.services.protos.Planting

data class GrpcPlanting(
    val id: Int,
    val name: String,
    val plant: GrpcPlant,
    val areaSqM: Int,
    val totalYield: GrpcPlantYield,
    val quantity: Int,
    val createdAt: Long,
    val readyToHarvestAt: Long,
    val diary: String,
    val harvested: Boolean
) {
    fun toNetwork(): Planting = Planting.newBuilder()
        .setId(id)
        .setName(name)
        .setPlant(plant.toNetwork())
        .setAreaSqM(areaSqM)
        .setTotalYield(totalYield.toNetwork())
        .setQuantity(quantity)
        .setCreatedAt(createdAt)
        .setReadyToHarvestAt(readyToHarvestAt)
        .setDiary(diary)
        .setHarvested(harvested)
        .build()

    companion object {
        fun fromNetwork(network: Planting): GrpcPlanting = GrpcPlanting(
            id = network.id,
            name = network.name,
            plant = GrpcPlant.fromNetwork(network.plant),
            areaSqM = network.areaSqM,
            totalYield = GrpcPlantYield.fromNetwork(network.totalYield),
            quantity = network.quantity,
            createdAt = network.createdAt,
            readyToHarvestAt = network.readyToHarvestAt,
            diary = network.diary,
            harvested = network.harvested
        )
    }
}