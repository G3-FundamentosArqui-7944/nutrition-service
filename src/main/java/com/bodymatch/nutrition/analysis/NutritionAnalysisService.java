package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.ai.NutritionAiAnalyzer;
import com.bodymatch.nutrition.client.IamGateway;
import com.bodymatch.nutrition.shared.MacroSummary;
import com.bodymatch.nutrition.shared.UserId;
import com.bodymatch.nutrition.storage.CloudStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NutritionAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NutritionAnalysisService.class);

    private final NutritionAnalysisRepository nutritionAnalysisRepository;
    private final CloudStorageService cloudStorageService;
    private final NutritionAiAnalyzer nutritionAiAnalyzer;
    private final IamGateway iamGateway;

    public NutritionAnalysisService(NutritionAnalysisRepository nutritionAnalysisRepository,
                                    CloudStorageService cloudStorageService,
                                    NutritionAiAnalyzer nutritionAiAnalyzer,
                                    IamGateway iamGateway) {
        this.nutritionAnalysisRepository = nutritionAnalysisRepository;
        this.cloudStorageService = cloudStorageService;
        this.nutritionAiAnalyzer = nutritionAiAnalyzer;
        this.iamGateway = iamGateway;
    }

    public Optional<NutritionAnalysis> findById(Long id) {
        return nutritionAnalysisRepository.findById(id);
    }

    public List<NutritionAnalysis> findByUser(UserId userId) {
        return nutritionAnalysisRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Optional<NutritionAnalysis> analyzeImage(Long userIdValue, String originalFilename, String contentType, byte[] imageContent) {
        var userId = new UserId(userIdValue);
        if (!iamGateway.existsUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + userIdValue);
        }
        if (imageContent == null || imageContent.length == 0) {
            throw new IllegalArgumentException("Image content is required");
        }
        var stored = cloudStorageService.upload(
                "nutrition/" + userIdValue,
                originalFilename,
                contentType,
                imageContent);
        var analysis = new NutritionAnalysis(userId, stored.storageKey(), stored.url());
        analysis.markProcessing();
        nutritionAnalysisRepository.save(analysis);

        try {
            var result = nutritionAiAnalyzer.analyzeFoodImage(contentType, imageContent);
            List<FoodDetection> detections = new ArrayList<>();
            for (var detected : result.detectedFoods()) {
                detections.add(new FoodDetection(
                        detected.foodName(),
                        detected.portionGrams(),
                        new MacroSummary(
                                detected.calories(),
                                detected.proteinGrams(),
                                detected.carbohydratesGrams(),
                                detected.fatGrams(),
                                detected.fiberGrams()),
                        detected.confidence()));
            }
            analysis.completeWith(result.summary(), result.aiModelVersion(), detections);
            nutritionAnalysisRepository.save(analysis);
            return Optional.of(analysis);
        } catch (RuntimeException e) {
            LOGGER.error("Nutrition analysis failed: {}", e.getMessage());
            analysis.markFailed(e.getMessage());
            nutritionAnalysisRepository.save(analysis);
            return Optional.of(analysis);
        }
    }
}
