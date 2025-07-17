package com.tometrics.api.services.achievements.config;

import com.tometrics.api.services.commonservice.DefaultJwtService;
import com.tometrics.api.services.commonservice.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Dotenv dotenv() {
        return new DotenvBuilder()
                .ignoreIfMalformed()
                .load();
    }

    @Bean
    public JwtService jwtService(Dotenv dotenv) {
        return new DefaultJwtService(dotenv);
    }

}
