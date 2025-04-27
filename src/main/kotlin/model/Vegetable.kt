package com.sproutscout.api.model

enum class Vegetable1(val plant: Plant) {
    TOMATO(Plant(
        id = 1,
        name = "Tomato",
        timeToHarvest = 60,
        bestMonths = listOf(3, 4, 5, 6, 7, 8),
        description = "A popular fruit vegetable that comes in many varieties and colors",
        yield = PlantYield(2, 5, YieldUnit.KG),
        yieldPerAreaM2 = PlantYield(3, 8, YieldUnit.KG),
        companionPlants = listOf(
            PlantRef(2, "Carrot"),
            PlantRef(3, "Lettuce"),
            PlantRef(16, "Onion")
        ),
        plantType = "Fruit",
        spacingFromCm = 45,
        spacingToCm = 60,
        sunlight = "Full sun",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Stake or cage plants for support",
            "Remove suckers for better fruit production",
            "Mulch to retain moisture"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(3, "Start seeds indoors"),
            MonthlyGuide(4, "Transplant seedlings"),
            MonthlyGuide(5, "Begin regular maintenance"),
            MonthlyGuide(6, "Harvest first fruits")
        )
    )),
    
    CARROT(Plant(
        id = 2,
        name = "Carrot",
        timeToHarvest = 70,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A root vegetable known for its orange color and sweet taste",
        yield = PlantYield(3, 5, YieldUnit.KG),
        yieldPerAreaM2 = PlantYield(4, 7, YieldUnit.KG),
        companionPlants = listOf(
            PlantRef(1, "Tomato"),
            PlantRef(3, "Lettuce"),
            PlantRef(12, "Peas")
        ),
        plantType = "Root",
        spacingFromCm = 5,
        spacingToCm = 8,
        sunlight = "Full sun to partial shade",
        soilNeeds = "Loose, sandy soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Thin seedlings to proper spacing",
            "Keep soil consistently moist",
            "Mulch to prevent soil crusting"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Direct sow seeds"),
            MonthlyGuide(3, "Thin seedlings"),
            MonthlyGuide(4, "Begin regular maintenance"),
            MonthlyGuide(5, "Harvest first carrots")
        )
    )),
    
    LETTUCE(Plant(
        id = 3,
        name = "Lettuce",
        timeToHarvest = 45,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A leafy green vegetable commonly used in salads",
        yield = PlantYield(1, 2, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(4, 6, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(2, "Carrot"),
            PlantRef(8, "Radish"),
            PlantRef(16, "Onion")
        ),
        plantType = "Leaf",
        spacingFromCm = 20,
        spacingToCm = 30,
        sunlight = "Partial shade to full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Plant in succession for continuous harvest",
            "Keep soil consistently moist",
            "Harvest outer leaves for continuous growth"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Direct sow seeds"),
            MonthlyGuide(3, "Begin harvesting outer leaves"),
            MonthlyGuide(4, "Succession planting"),
            MonthlyGuide(5, "Harvest full heads")
        )
    )),
    
    CUCUMBER(Plant(
        id = 4,
        name = "Cucumber",
        timeToHarvest = 50,
        bestMonths = listOf(4, 5, 6, 7, 8),
        description = "A refreshing vegetable that grows on vines",
        yield = PlantYield(10, 20, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(15, 25, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(12, "Peas"),
            PlantRef(16, "Onion")
        ),
        plantType = "Fruit",
        spacingFromCm = 30,
        spacingToCm = 45,
        sunlight = "Full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Provide trellis for vertical growth",
            "Harvest regularly to encourage more fruit",
            "Mulch to retain moisture"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(4, "Start seeds indoors"),
            MonthlyGuide(5, "Transplant seedlings"),
            MonthlyGuide(6, "Begin harvesting"),
            MonthlyGuide(7, "Regular maintenance")
        )
    )),
    
    BELL_PEPPER(Plant(
        id = 5,
        name = "Bell Pepper",
        timeToHarvest = 70,
        bestMonths = listOf(3, 4, 5, 6, 7, 8),
        description = "A sweet pepper that comes in various colors",
        yield = PlantYield(5, 10, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(8, 12, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(1, "Tomato"),
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic")
        ),
        plantType = "Fruit",
        spacingFromCm = 45,
        spacingToCm = 60,
        sunlight = "Full sun",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Support plants with stakes",
            "Pinch first flowers for better growth",
            "Harvest when firm and glossy"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(3, "Start seeds indoors"),
            MonthlyGuide(4, "Transplant seedlings"),
            MonthlyGuide(5, "Begin regular maintenance"),
            MonthlyGuide(6, "Harvest first peppers")
        )
    )),
    
    // Root Vegetables
    POTATO(Plant(
        id = 6,
        name = "Potato",
        timeToHarvest = 90,
        bestMonths = listOf(3, 4, 5, 6, 7, 8),
        description = "A starchy tuber vegetable that"s a staple in many cuisines",
        yield = PlantYield(2, 4, YieldUnit.KG),
        yieldPerAreaM2 = PlantYield(3, 6, YieldUnit.KG),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(12, "Peas"),
            PlantRef(15, "Cabbage")
        ),
        plantType = "Root",
        spacingFromCm = 30,
        spacingToCm = 40,
        sunlight = "Full sun",
        soilNeeds = "Well-drained, loose soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Hill soil around plants as they grow",
            "Keep soil consistently moist",
            "Harvest when foliage dies back"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(3, "Plant seed potatoes"),
            MonthlyGuide(4, "Begin hilling"),
            MonthlyGuide(5, "Regular maintenance"),
            MonthlyGuide(6, "Harvest new potatoes")
        )
    )),
    
    BEET(Plant(
        id = 7,
        name = "Beet",
        timeToHarvest = 55,
        bestMonths = listOf(3, 4, 5, 6, 7, 8, 9),
        description = "A root vegetable known for its deep red color and earthy flavor",
        yield = PlantYield(1, 2, YieldUnit.KG),
        yieldPerAreaM2 = PlantYield(3, 5, YieldUnit.KG),
        companionPlants = listOf(
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic"),
            PlantRef(3, "Lettuce")
        ),
        plantType = "Root",
        spacingFromCm = 10,
        spacingToCm = 15,
        sunlight = "Full sun to partial shade",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Thin seedlings for proper spacing",
            "Harvest when roots are 2-3 inches in diameter",
            "Both roots and greens are edible"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(3, "Direct sow seeds"),
            MonthlyGuide(4, "Thin seedlings"),
            MonthlyGuide(5, "Begin harvesting"),
            MonthlyGuide(6, "Succession planting")
        )
    )),
    
    RADISH(Plant(
        id = 8,
        name = "Radish",
        timeToHarvest = 25,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A fast-growing root vegetable with a peppery flavor",
        yield = PlantYield(10, 20, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(30, 40, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(3, "Lettuce"),
            PlantRef(9, "Spinach"),
            PlantRef(2, "Carrot")
        ),
        plantType = "Root",
        spacingFromCm = 5,
        spacingToCm = 8,
        sunlight = "Full sun to partial shade",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Quick growing - ready in 3-4 weeks",
            "Plant in succession for continuous harvest",
            "Harvest when roots are 1 inch in diameter"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Direct sow seeds"),
            MonthlyGuide(3, "Begin harvesting"),
            MonthlyGuide(4, "Succession planting"),
            MonthlyGuide(5, "Regular maintenance")
        )
    )),
    
    // Leafy Greens
    SPINACH(Plant(
        id = 9,
        name = "Spinach",
        timeToHarvest = 40,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A nutrient-rich leafy green vegetable",
        yield = PlantYield(1, 2, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(4, 6, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(12, "Peas"),
            PlantRef(8, "Radish")
        ),
        plantType = "Leaf",
        spacingFromCm = 15,
        spacingToCm = 20,
        sunlight = "Partial shade to full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Plant in succession for continuous harvest",
            "Harvest outer leaves for continuous growth",
            "Protect from heat in summer"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Direct sow seeds"),
            MonthlyGuide(3, "Begin harvesting"),
            MonthlyGuide(4, "Succession planting"),
            MonthlyGuide(5, "Regular maintenance")
        )
    )),
    
    KALE(Plant(
        id = 10,
        name = "Kale",
        timeToHarvest = 55,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A hardy leafy green vegetable rich in nutrients",
        yield = PlantYield(1, 2, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(3, 5, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(7, "Beet"),
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic")
        ),
        plantType = "Leaf",
        spacingFromCm = 30,
        spacingToCm = 45,
        sunlight = "Full sun to partial shade",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Harvest outer leaves for continuous growth",
            "Flavor improves after frost",
            "Can be grown in containers"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Start seeds indoors"),
            MonthlyGuide(3, "Transplant seedlings"),
            MonthlyGuide(4, "Begin harvesting"),
            MonthlyGuide(5, "Regular maintenance")
        )
    )),
    
    // Legumes
    GREEN_BEANS(Plant(
        id = 11,
        name = "Green Beans",
        timeToHarvest = 50,
        bestMonths = listOf(4, 5, 6, 7, 8),
        description = "A popular legume vegetable that grows on vines or bushes",
        yield = PlantYield(2, 4, YieldUnit.KG),
        yieldPerAreaM2 = PlantYield(3, 6, YieldUnit.KG),
        companionPlants = listOf(
            PlantRef(2, "Carrot"),
            PlantRef(4, "Cucumber"),
            PlantRef(6, "Potato")
        ),
        plantType = "Legume",
        spacingFromCm = 15,
        spacingToCm = 20,
        sunlight = "Full sun",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Provide support for pole varieties",
            "Harvest regularly to encourage more production",
            "Plant in succession for continuous harvest"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(4, "Direct sow seeds"),
            MonthlyGuide(5, "Begin harvesting"),
            MonthlyGuide(6, "Succession planting"),
            MonthlyGuide(7, "Regular maintenance")
        )
    )),
    
    PEAS(Plant(
        id = 12,
        name = "Peas",
        timeToHarvest = 60,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8),
        description = "A cool-season legume vegetable with edible pods",
        yield = PlantYield(1, 2, YieldUnit.KG),
        yieldPerAreaM2 = PlantYield(2, 4, YieldUnit.KG),
        companionPlants = listOf(
            PlantRef(2, "Carrot"),
            PlantRef(4, "Cucumber"),
            PlantRef(6, "Potato")
        ),
        plantType = "Legume",
        spacingFromCm = 5,
        spacingToCm = 8,
        sunlight = "Full sun to partial shade",
        waterNeeds = "Regular watering",
        soilNeeds = "Well-drained, fertile soil",
        growingTips = listOf(
            "Provide support for climbing varieties",
            "Plant early in spring",
            "Harvest when pods are plump"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Direct sow seeds"),
            MonthlyGuide(3, "Provide support"),
            MonthlyGuide(4, "Begin harvesting"),
            MonthlyGuide(5, "Regular maintenance")
        )
    )),
    
    // Brassicas
    BROCCOLI(Plant(
        id = 13,
        name = "Broccoli",
        timeToHarvest = 60,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A nutritious vegetable with edible flower heads",
        yield = PlantYield(1, 2, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(2, 4, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic")
        ),
        plantType = "Brassica",
        spacingFromCm = 45,
        spacingToCm = 60,
        sunlight = "Full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Harvest main head before flowers open",
            "Side shoots will continue to produce",
            "Protect from cabbage worms"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Start seeds indoors"),
            MonthlyGuide(3, "Transplant seedlings"),
            MonthlyGuide(4, "Begin harvesting"),
            MonthlyGuide(5, "Regular maintenance")
        )
    )),
    
    CAULIFLOWER(Plant(
        id = 14,
        name = "Cauliflower",
        timeToHarvest = 70,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A versatile vegetable with a mild flavor",
        yield = PlantYield(1, 2, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(2, 3, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic")
        ),
        plantType = "Brassica",
        spacingFromCm = 45,
        spacingToCm = 60,
        sunlight = "Full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Blanch heads by tying leaves over them",
            "Harvest when heads are firm and compact",
            "Protect from cabbage worms"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Start seeds indoors"),
            MonthlyGuide(3, "Transplant seedlings"),
            MonthlyGuide(4, "Begin blanching"),
            MonthlyGuide(5, "Harvest heads")
        )
    )),
    
    CABBAGE(Plant(
        id = 15,
        name = "Cabbage",
        timeToHarvest = 80,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A leafy vegetable that forms a dense head",
        yield = PlantYield(1, 2, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(2, 3, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic")
        ),
        plantType = "Brassica",
        spacingFromCm = 45,
        spacingToCm = 60,
        sunlight = "Full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Harvest when heads are firm",
            "Protect from cabbage worms",
            "Can be stored for months"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Start seeds indoors"),
            MonthlyGuide(3, "Transplant seedlings"),
            MonthlyGuide(4, "Regular maintenance"),
            MonthlyGuide(5, "Harvest heads")
        )
    )),
    
    // Alliums
    ONION(Plant(
        id = 16,
        name = "Onion",
        timeToHarvest = 100,
        bestMonths = listOf(2, 3, 4, 5, 6, 7, 8, 9),
        description = "A bulb vegetable used as a base in many dishes",
        yield = PlantYield(5, 10, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(15, 20, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(2, "Carrot"),
            PlantRef(3, "Lettuce"),
            PlantRef(1, "Tomato")
        ),
        plantType = "Allium",
        spacingFromCm = 10,
        spacingToCm = 15,
        sunlight = "Full sun",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Plant sets or seedlings for easier growth",
            "Harvest when tops fall over",
            "Cure before storage"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(2, "Plant sets or seedlings"),
            MonthlyGuide(3, "Regular maintenance"),
            MonthlyGuide(4, "Begin harvesting green onions"),
            MonthlyGuide(5, "Harvest mature bulbs")
        )
    )),
    
    GARLIC(Plant(
        id = 17,
        name = "Garlic",
        timeToHarvest = 240,
        bestMonths = listOf(9, 10, 11, 0, 1, 2, 3, 4, 5, 6),
        description = "A pungent bulb vegetable used for flavoring",
        yield = PlantYield(5, 10, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(15, 20, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(1, "Tomato"),
            PlantRef(5, "Bell Pepper"),
            PlantRef(2, "Carrot")
        ),
        plantType = "Allium",
        spacingFromCm = 15,
        spacingToCm = 20,
        sunlight = "Full sun",
        soilNeeds = "Well-drained, fertile soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Plant in fall for summer harvest",
            "Remove scapes for larger bulbs",
            "Cure before storage"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(9, "Plant cloves"),
            MonthlyGuide(10, "Mulch for winter"),
            MonthlyGuide(4, "Remove scapes"),
            MonthlyGuide(6, "Harvest bulbs")
        )
    )),
    
    // Squash Family
    ZUCCHINI(Plant(
        id = 18,
        name = "Zucchini",
        timeToHarvest = 50,
        bestMonths = listOf(4, 5, 6, 7, 8),
        description = "A summer squash that produces abundantly",
        yield = PlantYield(5, 10, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(8, 12, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(16, "Onion"),
            PlantRef(17, "Garlic")
        ),
        plantType = "Squash",
        spacingFromCm = 60,
        spacingToCm = 90,
        sunlight = "Full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Harvest when small for best flavor",
            "Check daily during peak production",
            "Provide plenty of space"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(4, "Start seeds indoors"),
            MonthlyGuide(5, "Transplant seedlings"),
            MonthlyGuide(6, "Begin harvesting"),
            MonthlyGuide(7, "Regular maintenance")
        )
    )),
    
    PUMPKIN(Plant(
        id = 19,
        name = "Pumpkin",
        timeToHarvest = 100,
        bestMonths = listOf(4, 5, 6, 7),
        description = "A winter squash often used for decoration and cooking",
        yield = PlantYield(2, 5, YieldUnit.UNIT),
        yieldPerAreaM2 = PlantYield(3, 6, YieldUnit.UNIT),
        companionPlants = listOf(
            PlantRef(11, "Green Beans"),
            PlantRef(12, "Peas"),
            PlantRef(16, "Onion")
        ),
        plantType = "Squash",
        spacingFromCm = 90,
        spacingToCm = 120,
        sunlight = "Full sun",
        soilNeeds = "Rich, well-drained soil",
        waterNeeds = "Regular watering",
        growingTips = listOf(
            "Provide plenty of space for vines",
            "Harvest before first frost",
            "Cure before storage"
        ),
        monthlyGuide = listOf(
            MonthlyGuide(4, "Start seeds indoors"),
            MonthlyGuide(5, "Transplant seedlings"),
            MonthlyGuide(6, "Regular maintenance"),
            MonthlyGuide(7, "Harvest pumpkins")
        )
    ))
} 