package com.tometrics.api.services.user.domain.models

data class UserWithSocialConnections(
    val user: User,
    val followers: List<User>,
    val following: List<User>,
)