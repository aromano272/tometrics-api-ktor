package com.tometrics.api.services.achievement;

import com.tometrics.api.services.achievement.config.ServiceDiscoveryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class AchievementServiceApp {

    public static void main(String[] args) {
        var serviceInfo = ServiceDiscoveryConfig.serviceInfo();

        var app = new SpringApplication(AchievementServiceApp.class);
        app.setDefaultProperties(Map.of("server.port", serviceInfo.getPort()));
        app.run(args);
    }
}
