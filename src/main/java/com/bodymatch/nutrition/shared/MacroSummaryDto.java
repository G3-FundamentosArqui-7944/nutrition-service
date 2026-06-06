package com.bodymatch.nutrition.shared;

import java.math.BigDecimal;

public record MacroSummaryDto(
        BigDecimal calories,
        BigDecimal proteinGrams,
        BigDecimal carbohydratesGrams,
        BigDecimal fatGrams,
        BigDecimal fiberGrams) {

    public static MacroSummaryDto from(MacroSummary macros) {
        if (macros == null) return new MacroSummaryDto(null, null, null, null, null);
        return new MacroSummaryDto(macros.calories(), macros.proteinGrams(),
                macros.carbohydratesGrams(), macros.fatGrams(), macros.fiberGrams());
    }

    public MacroSummary toValue() {
        return new MacroSummary(
                calories == null ? BigDecimal.ZERO : calories,
                proteinGrams == null ? BigDecimal.ZERO : proteinGrams,
                carbohydratesGrams == null ? BigDecimal.ZERO : carbohydratesGrams,
                fatGrams == null ? BigDecimal.ZERO : fatGrams,
                fiberGrams == null ? BigDecimal.ZERO : fiberGrams);
    }
}
