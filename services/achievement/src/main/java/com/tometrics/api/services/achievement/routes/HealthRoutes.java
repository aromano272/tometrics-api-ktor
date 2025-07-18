package com.tometrics.api.services.achievement.routes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRoutes {

    @GetMapping("/achievement/health")
    public ResponseEntity<Void> postAchievement() {
        return ResponseEntity.noContent().build();
    }

}
