package com.bodymatch.nutrition.plan;

import com.bodymatch.nutrition.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/nutrition/plans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Nutrition Plans", description = "Nutrition plan management")
public class NutritionPlanController {
    private final NutritionPlanService nutritionPlanService;

    public NutritionPlanController(NutritionPlanService nutritionPlanService) {
        this.nutritionPlanService = nutritionPlanService;
    }

    @PostMapping
    public ResponseEntity<NutritionPlanResponse> create(@RequestBody CreateNutritionPlanRequest request) {
        return nutritionPlanService.create(request)
                .map(NutritionPlanResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<NutritionPlanResponse> activeByUser(@PathVariable Long userId) {
        return nutritionPlanService.findActiveByUser(new UserId(userId))
                .map(NutritionPlanResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<NutritionPlanResponse> deactivate(@PathVariable Long planId) {
        return nutritionPlanService.deactivate(planId)
                .map(NutritionPlanResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
