package com.bodymatch.nutrition.shared;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record MacroSummary(
        BigDecimal calories,
        BigDecimal proteinGrams,
        BigDecimal carbohydratesGrams,
        BigDecimal fatGrams,
        BigDecimal fiberGrams) {

    public MacroSummary {
        if (calories == null || calories.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Calories must be non-negative");
        }
        if (proteinGrams == null || proteinGrams.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Protein must be non-negative");
        }
        if (carbohydratesGrams == null || carbohydratesGrams.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Carbohydrates must be non-negative");
        }
        if (fatGrams == null || fatGrams.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fat must be non-negative");
        }
        if (fiberGrams == null) {
            fiberGrams = BigDecimal.ZERO;
        }
    }

    public MacroSummary() {
        this(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public MacroSummary add(MacroSummary other) {
        return new MacroSummary(
                calories.add(other.calories),
                proteinGrams.add(other.proteinGrams),
                carbohydratesGrams.add(other.carbohydratesGrams),
                fatGrams.add(other.fatGrams),
                fiberGrams.add(other.fiberGrams));
    }
}
