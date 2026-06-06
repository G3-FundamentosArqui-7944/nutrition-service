package com.bodymatch.nutrition.plan;

import com.bodymatch.nutrition.client.IamGateway;
import com.bodymatch.nutrition.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NutritionPlanService {
    private final NutritionPlanRepository nutritionPlanRepository;
    private final IamGateway iamGateway;

    public NutritionPlanService(NutritionPlanRepository nutritionPlanRepository, IamGateway iamGateway) {
        this.nutritionPlanRepository = nutritionPlanRepository;
        this.iamGateway = iamGateway;
    }

    public Optional<NutritionPlan> findActiveByUser(UserId userId) {
        return nutritionPlanRepository.findByUserIdAndActiveTrue(userId);
    }

    @Transactional
    public Optional<NutritionPlan> create(CreateNutritionPlanRequest request) {
        var userId = new UserId(request.userId());
        if (!iamGateway.existsUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + request.userId());
        }
        nutritionPlanRepository.findByUserIdAndActiveTrue(userId).ifPresent(existing -> {
            existing.deactivate();
            nutritionPlanRepository.save(existing);
        });
        var plan = new NutritionPlan(userId, request.name(), request.description(),
                request.dailyTargets() == null ? new com.bodymatch.nutrition.shared.MacroSummary() : request.dailyTargets().toValue(),
                request.startDate(), request.endDate());
        nutritionPlanRepository.save(plan);
        return Optional.of(plan);
    }

    @Transactional
    public Optional<NutritionPlan> deactivate(Long planId) {
        var plan = nutritionPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        plan.deactivate();
        nutritionPlanRepository.save(plan);
        return Optional.of(plan);
    }
}
