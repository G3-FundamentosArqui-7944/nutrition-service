package com.bodymatch.nutrition.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiNutritionAnalyzer implements NutritionAiAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiNutritionAnalyzer.class);
    private static final String PROMPT = """
            You are a registered dietitian analyzing a meal photograph.

            Identify each visible food item, estimate portion size in grams, and the macronutrient breakdown.
            Respond ONLY with strict JSON matching this schema (no markdown, no commentary):
            {
              "summary": "string, 1-3 sentences",
              "detectedFoods": [
                {
                  "foodName": "string",
                  "portionGrams": number,
                  "calories": number,
                  "proteinGrams": number,
                  "carbohydratesGrams": number,
                  "fatGrams": number,
                  "fiberGrams": number,
                  "confidence": number between 0 and 1
                }
              ]
            }
            """;

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.model.nutrition:gemini-2.5-flash}")
    private String nutritionModel;

    public GeminiNutritionAnalyzer(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public NutritionAnalysisResult analyzeFoodImage(String mimeType, byte[] imageContent) {
        String raw = geminiClient.generateFromMultimodal(nutritionModel, PROMPT,
                mimeType != null ? mimeType : "image/jpeg", imageContent);
        return parseResponse(raw);
    }

    private NutritionAnalysisResult parseResponse(String raw) {
        try {
            String cleaned = stripCodeFences(raw);
            JsonNode root = objectMapper.readTree(cleaned);
            String summary = root.path("summary").asText("Analysis unavailable");
            List<NutritionAnalysisResult.DetectedFood> foods = new ArrayList<>();
            JsonNode detected = root.path("detectedFoods");
            if (detected.isArray()) {
                for (JsonNode node : detected) {
                    foods.add(new NutritionAnalysisResult.DetectedFood(
                            node.path("foodName").asText("unknown"),
                            decimal(node, "portionGrams"),
                            decimal(node, "calories"),
                            decimal(node, "proteinGrams"),
                            decimal(node, "carbohydratesGrams"),
                            decimal(node, "fatGrams"),
                            decimal(node, "fiberGrams"),
                            confidence(node)));
                }
            }
            return new NutritionAnalysisResult(summary, nutritionModel, foods);
        } catch (Exception e) {
            LOGGER.warn("Could not parse Gemini nutrition response: {}", e.getMessage());
            return new NutritionAnalysisResult(
                    "Automated analysis unavailable; raw response: " + truncate(raw),
                    nutritionModel,
                    List.of());
        }
    }

    private BigDecimal decimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isNumber()) return value.decimalValue().max(BigDecimal.ZERO);
        return BigDecimal.ZERO;
    }

    private BigDecimal confidence(JsonNode node) {
        JsonNode value = node.path("confidence");
        if (value.isNumber()) {
            return value.decimalValue().max(BigDecimal.ZERO).min(BigDecimal.ONE);
        }
        return BigDecimal.valueOf(0.5);
    }

    private String stripCodeFences(String text) {
        if (text == null) return "{}";
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline > -1) trimmed = trimmed.substring(firstNewline + 1);
            if (trimmed.endsWith("```")) trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private String truncate(String text) {
        if (text == null) return "";
        return text.length() > 500 ? text.substring(0, 500) : text;
    }
}
