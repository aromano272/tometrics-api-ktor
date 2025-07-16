package com.tometrics.api.services.achievements.db.models;

import com.tometrics.api.services.achievements.domain.models.AchievementType;
import com.tometrics.api.services.achievements.domain.models.UserAchievement;

public record UserAchievementEntity(
        int user_id,
        AchievementType type,
        int count
) {}
