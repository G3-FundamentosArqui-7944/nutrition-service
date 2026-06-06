package com.bodymatch.nutrition.ai;

public interface NutritionAiAnalyzer {
    NutritionAnalysisResult analyzeFoodImage(String mimeType, byte[] imageContent);
}
