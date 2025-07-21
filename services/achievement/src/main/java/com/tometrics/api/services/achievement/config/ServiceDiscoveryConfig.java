package com.tometrics.api.services.achievement.config;

import com.tometrics.api.common.domain.models.ServiceInfo;
import com.tometrics.api.common.domain.models.ServiceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for service discovery related beans.
 */
@Configuration
public class ServiceDiscoveryConfig {

    /**
     * Creates a ServiceInfo bean with the achievement service configuration.
     * This is used by the ServiceRegistrationComponent to log service information.
     * In a real implementation, it would be used to register the service with the service discovery.
     */
    @Bean
    static public ServiceInfo serviceInfo() {
        return new ServiceInfo(
            "/achievement",
            "localhost",
            8089,
            ServiceType.ACHIEVEMENT
        );
    }
}
