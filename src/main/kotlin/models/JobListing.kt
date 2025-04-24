package com.sproutscout.api.models

import kotlinx.serialization.Serializable

@Serializable
data class JobListing(
    val id: Int,
    val title: String,
    val company: String,
    val remote: RemoteOption,
    val type: JobType,
    val location: String,
    val createdAt: Millis,

    // Authenticated only fields
    val favorite: Boolean,
    val applied: Boolean,
)
