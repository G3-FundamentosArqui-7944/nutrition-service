package com.bodymatch.nutrition.analysis;

import com.bodymatch.nutrition.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutritionAnalysisRepository extends JpaRepository<NutritionAnalysis, Long> {
    List<NutritionAnalysis> findAllByUserIdOrderByCreatedAtDesc(UserId userId);
}
