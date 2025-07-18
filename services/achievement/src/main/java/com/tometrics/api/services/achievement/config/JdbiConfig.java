package com.tometrics.api.services.achievement.config;

import com.tometrics.api.services.achievement.db.AchievementDao;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbiConfig {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        var jdbi = Jdbi.create(dataSource);
        jdbi.installPlugins();

        return jdbi;
    }

    @Bean
    public AchievementDao achievementDao(Jdbi jdbi) {
        return jdbi.onDemand(AchievementDao.class);
    }

}
