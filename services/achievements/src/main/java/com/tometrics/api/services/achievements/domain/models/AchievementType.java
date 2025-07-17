package com.tometrics.api.services.achievements.domain.models;

import java.util.List;

public enum AchievementType {
    PLANTING_CREATED(List.of(
            new Step(1, 1),
            new Step(5, 5),
            new Step(10, 10),
            new Step(25, 25)
    )),
    HARVEST_CREATED(List.of(
            new Step(1, 1),
            new Step(5, 5),
            new Step(10, 10),
            new Step(25, 25)
    )),
    POST_CREATED(List.of(
            new Step(1, 1),
            new Step(5, 5),
            new Step(10, 10),
            new Step(25, 25)
    )),
    COMMENT_CREATED(List.of(
            new Step(1, 1),
            new Step(5, 5),
            new Step(10, 10),
            new Step(25, 25)
    )),
    REACTION_CREATED(List.of(
            new Step(1, 1),
            new Step(5, 5),
            new Step(10, 10),
            new Step(25, 25)
    ));

    public final List<Step> steps;

    AchievementType(List<Step> steps) {
        this.steps = steps;
    }

    public record Step(
            int count,
            int points
    ) {
    }

}
