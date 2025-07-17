package com.tometrics.api.services.achievements.routes;

import com.tometrics.api.services.achievements.domain.models.UserAchievement;
import com.tometrics.api.services.achievements.services.AchievementService;
import com.tometrics.api.services.commonservice.models.Requester;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievement")
public class AchievementRoutes {

    private final AchievementService achievementService;

    public AchievementRoutes(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping
    public ResponseEntity<Void> postAchievement(
        @RequestBody PostUserAchievementRequest request
    ) {
        achievementService.create(
                request.userId(),
                request.type()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserAchievement>> getAchievements(
            @AuthenticationPrincipal Requester requester
    ) {
        var result = achievementService.getAllAchievementsByUserId(requester.getUserId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<List<UserAchievement>> getAchievementsByUserId(
            @AuthenticationPrincipal Requester requester,
            @PathVariable int userId
    ) {
        var result = achievementService.getAllAchievementsByUserId(userId);
        return ResponseEntity.ok(result);
    }

}
