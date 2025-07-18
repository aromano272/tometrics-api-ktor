package com.tometrics.api.services.achievement.domain.models;

public record UserAchievement(
        AchievementType type,
        int count,
        int points
) {
}
