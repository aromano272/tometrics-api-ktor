package com.tometrics.api.services.achievement.services;

import com.tometrics.api.services.achievement.db.AchievementDao;
import com.tometrics.api.services.achievement.domain.models.AchievementType;
import com.tometrics.api.services.achievement.domain.models.UserAchievement;
import com.tometrics.api.services.commonservice.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementDao dao;
    private final RabbitMQProducer mqProducer;

    public AchievementService(
            AchievementDao achievementDao,
            RabbitMQProducer mqProducer
    ) {
        dao = achievementDao;
        this.mqProducer = mqProducer;
    }

    public void create(
            int userId,
            AchievementType type
    ) {
        dao.upsert(userId, type);
        var achievement = dao.findByUserIdAndType(userId, type);

        var matchStep = achievement.type().steps.stream()
                .filter(step -> step.count() == achievement.count())
                .findFirst();
        if (matchStep.isPresent()) {
            var eventAchType = switch (achievement.type()) {
                case AchievementType.PLANTING_CREATED ->
                        com.tometrics.api.services.commonservice.AchievementType.PLANTING_CREATED;
                case AchievementType.HARVEST_CREATED ->
                        com.tometrics.api.services.commonservice.AchievementType.HARVEST_CREATED;
                case AchievementType.POST_CREATED ->
                        com.tometrics.api.services.commonservice.AchievementType.POST_CREATED;
                case AchievementType.COMMENT_CREATED ->
                        com.tometrics.api.services.commonservice.AchievementType.COMMENT_CREATED;
                case AchievementType.REACTION_CREATED ->
                        com.tometrics.api.services.commonservice.AchievementType.REACTION_CREATED;
            };
            var event = new Message.AchievementEarned(
                    eventAchType,
                    userId,
                    achievement.count(),
                    matchStep.get().points()
            );
            mqProducer.send(event);
        }
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
