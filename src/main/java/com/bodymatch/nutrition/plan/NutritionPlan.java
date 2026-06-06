package com.bodymatch.nutrition.plan;

import com.bodymatch.nutrition.shared.AuditableAbstractAggregateRoot;
import com.bodymatch.nutrition.shared.MacroSummary;
import com.bodymatch.nutrition.shared.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
public class NutritionPlan extends AuditableAbstractAggregateRoot<NutritionPlan> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Getter
    @Column(nullable = false, length = 120)
    private String name;

    @Getter
    @Column(length = 2000)
    private String description;

    @Embedded
    @Getter
    @AttributeOverride(name = "calories", column = @Column(name = "daily_calories"))
    @AttributeOverride(name = "proteinGrams", column = @Column(name = "daily_protein_grams"))
    @AttributeOverride(name = "carbohydratesGrams", column = @Column(name = "daily_carbohydrates_grams"))
    @AttributeOverride(name = "fatGrams", column = @Column(name = "daily_fat_grams"))
    @AttributeOverride(name = "fiberGrams", column = @Column(name = "daily_fiber_grams"))
    private MacroSummary dailyTargets;

    @Getter
    @Column(nullable = false)
    private Instant startDate;

    @Getter
    private Instant endDate;

    @Getter
    @Column(nullable = false)
    private boolean active;

    public NutritionPlan(UserId userId, String name, String description,
                         MacroSummary dailyTargets, Instant startDate, Instant endDate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Plan name required");
        }
        if (dailyTargets == null) {
            throw new IllegalArgumentException("Daily targets required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date required");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.dailyTargets = dailyTargets;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
        this.endDate = Instant.now();
    }
}
