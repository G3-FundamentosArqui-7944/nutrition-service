package com.bodymatch.nutrition.plan;

import com.bodymatch.nutrition.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutritionPlanRepository extends JpaRepository<NutritionPlan, Long> {
    Optional<NutritionPlan> findByUserIdAndActiveTrue(UserId userId);
}
