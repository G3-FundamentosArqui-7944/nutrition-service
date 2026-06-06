package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/nutrition/analyses", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Nutrition Analyses", description = "Food image analysis via Gemini AI")
public class NutritionAnalysisController {
    private final NutritionAnalysisService nutritionAnalysisService;

    public NutritionAnalysisController(NutritionAnalysisService nutritionAnalysisService) {
        this.nutritionAnalysisService = nutritionAnalysisService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NutritionAnalysisResponse> analyzeImage(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return nutritionAnalysisService.analyzeImage(userId, file.getOriginalFilename(), file.getContentType(), file.getBytes())
                .map(NutritionAnalysisResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<NutritionAnalysisResponse> getById(@PathVariable Long analysisId) {
        return nutritionAnalysisService.findById(analysisId)
                .map(NutritionAnalysisResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NutritionAnalysisResponse>> byUser(@PathVariable Long userId) {
        var analyses = nutritionAnalysisService.findByUser(new UserId(userId))
                .stream().map(NutritionAnalysisResponse::from).toList();
        return ResponseEntity.ok(analyses);
    }
}
