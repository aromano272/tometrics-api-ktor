package com.sproutscout.api.service

import com.sproutscout.api.database.UserDao
import com.sproutscout.api.database.models.toDomain
import com.sproutscout.api.domain.models.User
import com.sproutscout.api.service.templates.HarvestNotificationTemplate
import io.ktor.util.logging.*

interface CronjobService {
    suspend fun checkForDailyHarvests()
}

class DefaultCronjobService(
    private val gardenService: GardenService,
    private val emailService: EmailService,
    private val userDao: UserDao,
    private val logger: Logger,
) : CronjobService {

    override suspend fun checkForDailyHarvests() {
        val readyForHarvest = gardenService.getAllReadyForHarvestToday()

//        val planting = Planting(
//            1,
//            plantsMap["ORACH"]!!,
//            1,
//            PlantYield(1f, 1f, YieldUnit.UNIT),
//            1283109301238
//        )
//        val readyForHarvest = mapOf(1 to listOf(planting))

        readyForHarvest.forEach { (userId, plantings) ->
            val user = userDao.findById(userId)?.toDomain()
                ?: User(1, "name", "google@gmail.com", null, null, false)
                ?: return@forEach

            val email = user.idpGoogleEmail ?: user.idpFacebookEmail
                ?: return@forEach

            try {
                val template = HarvestNotificationTemplate(
                    plantings = plantings
                )
                
                emailService.sendEmail(
                    to = email,
                    subject = "🌱 Your Plants Are Ready for Harvest!",
                    template = template
                )
                
                logger.info("Sent harvest notification email to $email")
            } catch (e: Exception) {
                logger.error("Failed to send harvest notification email to $email: ${e.message}")
            }
        }
    }
}