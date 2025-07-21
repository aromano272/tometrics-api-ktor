package com.tometrics.api.services.achievement.routes.models;

import com.tometrics.api.services.achievement.domain.models.AchievementType;

// TODO(aromano): debug
public record PostUserAchievementRequest(
        int userId,
        AchievementType type
) {
}
