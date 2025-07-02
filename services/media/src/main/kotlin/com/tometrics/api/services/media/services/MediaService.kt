package com.tometrics.api.services.media.services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import io.github.cdimascio.dotenv.Dotenv
import java.util.*
import kotlin.time.Duration.Companion.minutes

interface MediaService {
    suspend fun generatePresignedUrl(requester: Requester): String
}

class DefaultMediaService(
    private val dotenv: Dotenv,
    private val s3Client: S3Client,
    private val userGrpcClient: UserGrpcClient,
) : MediaService {

    override suspend fun generatePresignedUrl(requester: Requester): String {
        userGrpcClient.validateUserIds(setOf(requester.userId))
            .takeIf { it !is GrpcValidateUsersResult.Success }
            ?.let { throw UnauthorizedError("") }

        val userUploadBucket = dotenv["S3_BUCKET_USER_UPLOAD"]
        val unsignedRequest = GetObjectRequest {
            bucket = userUploadBucket
            key = UUID.randomUUID().toString()
        }

        val presignedRequest = s3Client.presignGetObject(unsignedRequest, 15.minutes)
        return presignedRequest.url.toString()
    }

}