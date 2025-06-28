package com.tometrics.api.services.cronjob.services

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

    // TODO(aromano): user-refactor
    override suspend fun checkForDailyHarvests() {
        val readyForHarvest = gardenService.getAllReadyForHarvestToday()

        readyForHarvest.forEach { (userId, plantings) ->
            val user = userDao.findById(userId)?.toDomain()
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