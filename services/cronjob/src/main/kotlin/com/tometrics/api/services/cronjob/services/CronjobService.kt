package com.tometrics.api.services.cronjob.services

import com.tometrics.api.services.commongrpc.services.GardenGrpcClient
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import io.ktor.util.logging.*

interface CronjobService {
//    suspend fun checkForDailyHarvests()
}

class DefaultCronjobService(
    private val gardenGrpcClient: GardenGrpcClient,
    // TODO(aromano): probably send emailtemplates to a MQ that emailservice listens to rather than directly calling it
//    private val emailGrpcClient: EmailGrpcClient,
    private val userGrpcClient: UserGrpcClient,
    private val logger: Logger,
) : CronjobService {

    // TODO(aromano): user-refactor
//    override suspend fun checkForDailyHarvests() {
//        val readyForHarvest = gardenGrpcClient.getAllReadyForHarvestToday()
//
//        readyForHarvest.forEach { (userId, plantings) ->
//            val user = userGrpcClient.findById(userId)?.toDomain()
//                ?: return@forEach
//
//            val email = user.idpGoogleEmail ?: user.idpFacebookEmail
//            ?: return@forEach
//
//            try {
//                val template = HarvestNotificationTemplate(
//                    plantings = plantings
//                )
//
//                emailService.sendEmail(
//                    to = email,
//                    subject = "🌱 Your Plants Are Ready for Harvest!",
//                    template = template
//                )
//
//                logger.info("Sent harvest notification email to $email")
//            } catch (e: Exception) {
//                logger.error("Failed to send harvest notification email to $email: ${e.message}")
//            }
//        }
//    }
}