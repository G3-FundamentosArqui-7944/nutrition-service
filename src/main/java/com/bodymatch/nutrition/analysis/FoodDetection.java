package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.shared.AuditableModel;
import com.bodymatch.nutrition.shared.MacroSummary;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
public class FoodDetection extends AuditableModel {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false, length = 120)
    private String foodName;

    @Getter
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal portionGrams;

    @Embedded
    @Getter
    @AttributeOverride(name = "calories", column = @Column(name = "calories", nullable = false))
    @AttributeOverride(name = "proteinGrams", column = @Column(name = "protein_grams", nullable = false))
    @AttributeOverride(name = "carbohydratesGrams", column = @Column(name = "carbohydrates_grams", nullable = false))
    @AttributeOverride(name = "fatGrams", column = @Column(name = "fat_grams", nullable = false))
    @AttributeOverride(name = "fiberGrams", column = @Column(name = "fiber_grams", nullable = false))
    private MacroSummary macros;

    @Getter
    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal confidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_analysis_id")
    @Getter
    @Setter
    private NutritionAnalysis nutritionAnalysis;

    public FoodDetection(String foodName, BigDecimal portionGrams, MacroSummary macros, BigDecimal confidence) {
        if (foodName == null || foodName.isBlank()) {
            throw new IllegalArgumentException("Food name is required");
        }
        if (portionGrams == null || portionGrams.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Portion must be non-negative");
        }
        if (confidence == null || confidence.compareTo(BigDecimal.ZERO) < 0 || confidence.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Confidence must be between 0 and 1");
        }
        this.foodName = foodName;
        this.portionGrams = portionGrams;
        this.macros = macros == null ? new MacroSummary() : macros;
        this.confidence = confidence;
    }
}
