package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.shared.MacroSummaryDto;

import java.math.BigDecimal;

public record FoodDetectionResponse(
        Long id,
        String foodName,
        BigDecimal portionGrams,
        MacroSummaryDto macros,
        BigDecimal confidence) {

    public static FoodDetectionResponse from(FoodDetection d) {
        return new FoodDetectionResponse(
                d.getId(),
                d.getFoodName(),
                d.getPortionGrams(),
                MacroSummaryDto.from(d.getMacros()),
                d.getConfidence());
    }
}
