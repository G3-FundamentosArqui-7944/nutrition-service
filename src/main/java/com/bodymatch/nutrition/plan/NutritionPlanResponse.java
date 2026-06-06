package com.bodymatch.nutrition.plan;

import com.bodymatch.nutrition.shared.MacroSummaryDto;

import java.time.Instant;

public record NutritionPlanResponse(
        Long id,
        Long userId,
        String name,
        String description,
        MacroSummaryDto dailyTargets,
        Instant startDate,
        Instant endDate,
        boolean active) {

    public static NutritionPlanResponse from(NutritionPlan plan) {
        return new NutritionPlanResponse(
                plan.getId(),
                plan.getUserId().userId(),
                plan.getName(),
                plan.getDescription(),
                MacroSummaryDto.from(plan.getDailyTargets()),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.isActive());
    }
}
