package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.shared.AuditableAbstractAggregateRoot;
import com.bodymatch.nutrition.shared.MacroSummary;
import com.bodymatch.nutrition.shared.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class NutritionAnalysis extends AuditableAbstractAggregateRoot<NutritionAnalysis> {

    @Embedded
    @Getter
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Getter
    @Column(nullable = false, length = 500)
    private String imageStorageKey;

    @Getter
    @Column(nullable = false, length = 1000)
    private String imageStorageUrl;

    @Getter
    @Column(length = 4000)
    private String summary;

    @Embedded
    @Getter
    @AttributeOverride(name = "calories", column = @Column(name = "total_calories"))
    @AttributeOverride(name = "proteinGrams", column = @Column(name = "total_protein_grams"))
    @AttributeOverride(name = "carbohydratesGrams", column = @Column(name = "total_carbohydrates_grams"))
    @AttributeOverride(name = "fatGrams", column = @Column(name = "total_fat_grams"))
    @AttributeOverride(name = "fiberGrams", column = @Column(name = "total_fiber_grams"))
    private MacroSummary totalMacros;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NutritionAnalysisStatus status;

    @Getter
    @Column(length = 500)
    private String failureReason;

    @Getter
    @Column(length = 80)
    private String aiModelVersion;

    @Getter
    @Column
    private Instant analyzedAt;

    @OneToMany(mappedBy = "nutritionAnalysis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Getter
    private List<FoodDetection> detectedFoods;

    public NutritionAnalysis(UserId userId, String imageStorageKey, String imageStorageUrl) {
        if (imageStorageKey == null || imageStorageKey.isBlank() || imageStorageUrl == null || imageStorageUrl.isBlank()) {
            throw new IllegalArgumentException("Image storage key and url required");
        }
        this.userId = userId;
        this.imageStorageKey = imageStorageKey;
        this.imageStorageUrl = imageStorageUrl;
        this.status = NutritionAnalysisStatus.PENDING;
        this.detectedFoods = new ArrayList<>();
        this.totalMacros = new MacroSummary();
    }

    public void markProcessing() {
        if (status == NutritionAnalysisStatus.COMPLETED) return;
        this.status = NutritionAnalysisStatus.PROCESSING;
        this.failureReason = null;
    }

    public void completeWith(String summary, String aiModelVersion, List<FoodDetection> detections) {
        if (detections == null) {
            throw new IllegalArgumentException("Detections required");
        }
        this.summary = summary;
        this.aiModelVersion = aiModelVersion;
        this.analyzedAt = Instant.now();
        this.detectedFoods.clear();
        var aggregated = new MacroSummary();
        for (var detection : detections) {
            detection.setNutritionAnalysis(this);
            this.detectedFoods.add(detection);
            aggregated = aggregated.add(detection.getMacros());
        }
        this.totalMacros = aggregated;
        this.status = NutritionAnalysisStatus.COMPLETED;
        this.failureReason = null;
    }

    public void markFailed(String reason) {
        this.status = NutritionAnalysisStatus.FAILED;
        this.failureReason = reason;
    }
}
