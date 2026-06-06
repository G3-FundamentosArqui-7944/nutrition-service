package com.bodymatch.nutrition.meal;

import com.bodymatch.nutrition.shared.MacroSummaryDto;

import java.time.Instant;

public record LogMealRequest(
        Long userId,
        String mealType,
        String description,
        MacroSummaryDto macros,
        Instant consumedAt,
        Long sourceAnalysisId) {
}
