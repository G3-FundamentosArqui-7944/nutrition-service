package com.bodymatch.nutrition.meal;

import com.bodymatch.nutrition.shared.AuditableAbstractAggregateRoot;
import com.bodymatch.nutrition.shared.MacroSummary;
import com.bodymatch.nutrition.shared.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
public class MealRecord extends AuditableAbstractAggregateRoot<MealRecord> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MealType mealType;

    @Getter
    @Column(nullable = false, length = 200)
    private String description;

    @Embedded
    @Getter
    @AttributeOverride(name = "calories", column = @Column(name = "calories", nullable = false))
    @AttributeOverride(name = "proteinGrams", column = @Column(name = "protein_grams", nullable = false))
    @AttributeOverride(name = "carbohydratesGrams", column = @Column(name = "carbohydrates_grams", nullable = false))
    @AttributeOverride(name = "fatGrams", column = @Column(name = "fat_grams", nullable = false))
    @AttributeOverride(name = "fiberGrams", column = @Column(name = "fiber_grams", nullable = false))
    private MacroSummary macros;

    @Getter
    @Column(nullable = false)
    private Instant consumedAt;

    @Getter
    @Column
    private Long sourceAnalysisId;

    public MealRecord(UserId userId, MealType mealType, String description, MacroSummary macros,
                      Instant consumedAt, Long sourceAnalysisId) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Meal description required");
        }
        this.userId = userId;
        this.mealType = mealType == null ? MealType.SNACK : mealType;
        this.description = description;
        this.macros = macros == null ? new MacroSummary() : macros;
        this.consumedAt = consumedAt == null ? Instant.now() : consumedAt;
        this.sourceAnalysisId = sourceAnalysisId;
    }
}
