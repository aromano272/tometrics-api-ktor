package com.tometrics.api.services.achievements.domain.models;

public record UserAchievement(
        AchievementType type,
        int count,
        int points
) {
}
