package com.tometrics.api.services.achievements.routes;

import com.tometrics.api.services.achievements.domain.models.AchievementType;
import com.tometrics.api.services.achievements.domain.models.UserAchievement;
import com.tometrics.api.services.achievements.services.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AchievementsRoutes {

    private final AchievementService achievementService;

    public AchievementsRoutes(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping("/api/v1/achievement")
    public ResponseEntity<Void> postAchievement(
        @RequestBody PostUserAchievementRequest request
    ) {
        achievementService.create(
                request.userId(),
                request.type()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/achievement/{userId}/all")
    public ResponseEntity<List<UserAchievement>> getAchievementsByUserId(
            @PathVariable int userId
    ) {
        var result = achievementService.getAllAchievementsByUserId(userId);
        return ResponseEntity.ok(result);
    }

}
