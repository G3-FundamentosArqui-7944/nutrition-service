package com.bodymatch.nutrition.ai;

import java.math.BigDecimal;
import java.util.List;

public record NutritionAnalysisResult(
        String summary,
        String aiModelVersion,
        List<DetectedFood> detectedFoods) {

    public record DetectedFood(
            String foodName,
            BigDecimal portionGrams,
            BigDecimal calories,
            BigDecimal proteinGrams,
            BigDecimal carbohydratesGrams,
            BigDecimal fatGrams,
            BigDecimal fiberGrams,
            BigDecimal confidence) {
    }
}
