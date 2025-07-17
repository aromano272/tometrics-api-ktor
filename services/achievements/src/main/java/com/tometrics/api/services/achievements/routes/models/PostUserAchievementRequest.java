package com.tometrics.api.services.achievements.routes.models;

import com.tometrics.api.services.achievements.domain.models.AchievementType;

// TODO(aromano): debug
public record PostUserAchievementRequest(
        int userId,
        AchievementType type
) {}
