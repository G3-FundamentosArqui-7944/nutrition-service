package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.shared.MacroSummaryDto;

import java.time.Instant;
import java.util.List;

public record NutritionAnalysisResponse(
        Long id,
        Long userId,
        String imageStorageUrl,
        String summary,
        MacroSummaryDto totalMacros,
        String status,
        String failureReason,
        String aiModelVersion,
        Instant analyzedAt,
        List<FoodDetectionResponse> detectedFoods) {

    public static NutritionAnalysisResponse from(NutritionAnalysis a) {
        var detections = a.getDetectedFoods().stream().map(FoodDetectionResponse::from).toList();
        return new NutritionAnalysisResponse(
                a.getId(),
                a.getUserId().userId(),
                a.getImageStorageUrl(),
                a.getSummary(),
                MacroSummaryDto.from(a.getTotalMacros()),
                a.getStatus().name(),
                a.getFailureReason(),
                a.getAiModelVersion(),
                a.getAnalyzedAt(),
                detections);
    }
}
