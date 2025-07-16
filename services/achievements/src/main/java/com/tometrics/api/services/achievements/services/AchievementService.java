package com.tometrics.api.services.achievements.services;

import com.tometrics.api.services.achievements.db.AchievementDao;
import com.tometrics.api.services.achievements.domain.models.AchievementType;
import com.tometrics.api.services.achievements.domain.models.UserAchievement;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementDao dao;

    public AchievementService(
            AchievementDao achievementDao
    ) {
        dao = achievementDao;
    }

    public void create(
            int userId,
            AchievementType type
    ) {
        dao.upsert(userId, type);
    }

    public List<UserAchievement> getAllAchievementsByUserId(int userId) {
        var entities = dao.getAllAchievementsByUserId(userId);
        return entities.stream()
                .map(entity -> {
                    var points = 0;
                    for (var step : entity.type().steps) {
                        if (entity.count() < step.count()) break;
                        points += step.points();
                    }
                    var domain = new UserAchievement(
                            entity.type(),
                            entity.count(),
                            points
                    );
                    return domain;
                }).toList();
    }

}
