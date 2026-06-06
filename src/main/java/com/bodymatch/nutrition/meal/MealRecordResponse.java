package com.bodymatch.nutrition.meal;

import com.bodymatch.nutrition.shared.MacroSummaryDto;

import java.time.Instant;

public record MealRecordResponse(
        Long id,
        Long userId,
        String mealType,
        String description,
        MacroSummaryDto macros,
        Instant consumedAt,
        Long sourceAnalysisId) {

    public static MealRecordResponse from(MealRecord meal) {
        return new MealRecordResponse(
                meal.getId(),
                meal.getUserId().userId(),
                meal.getMealType().name(),
                meal.getDescription(),
                MacroSummaryDto.from(meal.getMacros()),
                meal.getConsumedAt(),
                meal.getSourceAnalysisId());
    }
}
