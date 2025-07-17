package com.tometrics.api.services.achievements.db.models;

import com.tometrics.api.services.achievements.domain.models.AchievementType;

public record UserAchievementEntity(
        int user_id,
        AchievementType type,
        int count
) {
}
