package com.tometrics.api.routes

/*

type Plant
    popularity: Int

type Planting(todo naming, isto eh basicamente uma "plantacao" de um vegetal numa data especifica)
    Plant
    plantDate
    etc..

My Garden
GET /garden RES: {garden: List<Planting>}
DELETE /planting/{id}
PATCH /planting/{id} REQ: {newQuantity: Int} RES: {planting: Planting}
POST /garden/addplant
    REQ: { plantId: Int, quantity: Int }

type CropCalendarEntry
    Plant
    plantingMonths: List<Int>

Planting Calendar
GET /cropcalendar?search=%s&PAGINATION RES: {entries: [CropCalendarEntry], PAGINATION}

Veggie Guide
GET /plant/all
    RES: {plants: List<Plant>}

GET /plant/{id}
    RES: Plant

type GardenCell
    x: Int
    y: Int
    plant: Plant?

type GardenSummary
    vai ter em conta o espacamento entre as plantas

TODO watering reminder?

Garden Layout Designer
GET /designer
    RES: {garden: Array<Array<GardenGell>>, summary: GardenSummary}

PUT /designer
    REQ: {garden: Array<Array<GardenGell>>}
















 */