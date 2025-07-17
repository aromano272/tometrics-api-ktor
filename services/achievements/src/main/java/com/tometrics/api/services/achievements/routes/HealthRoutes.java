package com.tometrics.api.services.achievements.routes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthRoutes {

    @GetMapping("/achievement/health")
    public ResponseEntity<Void> postAchievement() {
        return ResponseEntity.noContent().build();
    }

}
