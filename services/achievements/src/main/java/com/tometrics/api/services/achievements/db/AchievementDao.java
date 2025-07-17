package com.tometrics.api.services.achievements.db;

import com.tometrics.api.services.achievements.db.models.UserAchievementEntity;
import com.tometrics.api.services.achievements.domain.models.AchievementType;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RegisterConstructorMapper(UserAchievementEntity.class)
public interface AchievementDao {

    @SqlUpdate("""
                    INSERT INTO achievements (user_id, type, count)
                    VALUES (:userId, :type, 1)
                    ON CONFLICT (user_id, type)
                    DO UPDATE SET count = achievements.count + 1,
                    updated_at = NOW();
            """)
    void upsert(
            @Bind("userId") int userId,
            @Bind("type") AchievementType type
    );

    @SqlQuery("SELECT * FROM achievements WHERE user_id = :userId")
    List<UserAchievementEntity> getAllAchievementsByUserId(@Bind("userId") int userId);

}
