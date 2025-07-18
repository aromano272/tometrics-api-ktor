package com.tometrics.api.services.achievement.db.models;

import com.tometrics.api.services.achievement.domain.models.AchievementType;

public record UserAchievementEntity(
        int user_id,
        AchievementType type,
        int count
) {
}
