package com.bodymatch.nutrition.meal;

import com.bodymatch.nutrition.shared.MacroSummaryDto;
import com.bodymatch.nutrition.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/nutrition/meals", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Meals", description = "Meal logging and macro tracking")
public class MealController {
    private final MealRecordService mealRecordService;

    public MealController(MealRecordService mealRecordService) {
        this.mealRecordService = mealRecordService;
    }

    @PostMapping
    public ResponseEntity<MealRecordResponse> logMeal(@RequestBody LogMealRequest request) {
        return mealRecordService.log(request)
                .map(MealRecordResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealRecordResponse>> byUser(
            @PathVariable Long userId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        var meals = mealRecordService.findByUser(new UserId(userId), from, to)
                .stream().map(MealRecordResponse::from).toList();
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/user/{userId}/daily-summary")
    public ResponseEntity<MacroSummaryDto> dailySummary(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(MacroSummaryDto.from(mealRecordService.dailySummaryFor(new UserId(userId), date)));
    }
}
