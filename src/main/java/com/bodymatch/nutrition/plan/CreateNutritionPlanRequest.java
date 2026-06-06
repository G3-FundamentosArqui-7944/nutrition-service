package com.bodymatch.nutrition.plan;

import com.bodymatch.nutrition.shared.MacroSummaryDto;

import java.time.Instant;

public record CreateNutritionPlanRequest(
        Long userId,
        String name,
        String description,
        MacroSummaryDto dailyTargets,
        Instant startDate,
        Instant endDate) {
}
