package com.tometrics.api.services.garden.services

import com.tometrics.api.services.garden.domain.models.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException

interface DesignerService {
    suspend fun get(): GardenDesign
    suspend fun update(cells: List<List<GardenCellRef>>): GardenDesign
}

class DefaultDesignerService(
    private val plantService: PlantService,
) : DesignerService {
    private var garden: GardenDesign = GardenDesign(emptyList(), emptyList(), emptyList())

    override suspend fun get(): GardenDesign = garden

    override suspend fun update(cells: List<List<GardenCellRef>>): GardenDesign {
        val numRows = cells.size
        val numCols = cells.firstOrNull()?.size ?: 0
        val visited = Array(numRows) { BooleanArray(numCols) }
        val plantedContiguous = mutableListOf<ContiguousCellRefs>()
        val plantIds = mutableSetOf<PlantId>()

        val directions = listOf(
            -1 to 0, 1 to 0, 0 to -1, 0 to 1
        )

        fun dfs(x: Int, y: Int, value: GardenCellRef): ContiguousCellRefs {
            val stack = ArrayDeque<GardenCellRef>()
            stack.add(value)
            visited[y][x] = true
            val result = mutableListOf<GardenCellRef>()

            while (stack.isNotEmpty()) {
                val el = stack.removeLast()
                val x = el.x
                val y = el.y

                result += el
                if (el.plantId != null) {
                    plantIds += el.plantId
                }

                for ((dx, dy) in directions) {
                    val nx = x + dx
                    val ny = y + dy
                    if (ny in 0 until numRows && nx in 0 until numCols
                        && !visited[ny][nx] && cells[ny][nx].plantId == value.plantId
                    ) {
                        visited[ny][nx] = true
                        stack.add(cells[ny][nx])
                    }
                }
            }

            return result
        }

        cells.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell.x != x || cell.y != y) throw BadRequestException("Cells are not ordered")
                if (!visited[y][x] && cell.plantId != null) {
                    val result = dfs(x, y, cell)
                    plantedContiguous += result
                }
            }
        }

        val plants: Map<PlantId, Plant> = plantService.getAllByIds(plantIds).associateBy { it.id }
        val resolvedCells = cells.map { rows ->
            rows.map { cell ->
                val plant = plants[cell.plantId]
                if (cell.plantId != null && plant == null) throw NotFoundException("Plant #${cell.plantId} not found")
                cell.toDomain(plant)
            }
        }
        val resolvedPlantedContiguous = plantedContiguous.map { list ->
            list.map { cell ->
                cell.toDomain(plants[cell.plantId])
            }
        }

        garden = GardenDesign(
            cells = resolvedCells,
            plantedContiguous = resolvedPlantedContiguous,
            summary = resolvedPlantedContiguous
                .groupBy { it.first().plant!! }
                .map { (plant, groups) ->
                    var totalPlantYield = PlantingYield(plant, 0, PlantYield(0f, 0f, plant.yieldPerPlant.unit))
                    groups.forEach { planted ->
                        val areaSqM = planted.size
                        val quantity = plant.quantityPerAreaSqM(areaSqM).toInt()
                        totalPlantYield += PlantingYield(
                            plant = plant,
                            // TODO this quantity method most likely isn't inline with the plant.yieldPerSqM
                            //      meaning that: quantity * plant.yield != area * plant.yieldPerSqM
                            //      But we probably wanna keep the yieldPerSqM because this is always in weight
                            //      and never in units, unlike plant.yieldPerPlant
                            // UPDATE actually yieldPerSqM doens't take into consideration optimizations with
                            // adjacent same-plant cells, we should stick with using the calculated quantity
                            quantity = quantity,
                            yield = plant.yieldPerPlant * quantity,
                        )
                    }
                    totalPlantYield
                },
        )

        return garden
    }
}