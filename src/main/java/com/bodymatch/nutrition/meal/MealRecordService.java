package com.bodymatch.nutrition.meal;

import com.bodymatch.nutrition.client.IamGateway;
import com.bodymatch.nutrition.shared.MacroSummary;
import com.bodymatch.nutrition.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class MealRecordService {
    private final MealRecordRepository mealRecordRepository;
    private final IamGateway iamGateway;

    public MealRecordService(MealRecordRepository mealRecordRepository, IamGateway iamGateway) {
        this.mealRecordRepository = mealRecordRepository;
        this.iamGateway = iamGateway;
    }

    public List<MealRecord> findByUser(UserId userId, Instant from, Instant to) {
        if (from != null && to != null) {
            return mealRecordRepository.findAllByUserIdAndConsumedAtBetweenOrderByConsumedAtDesc(userId, from, to);
        }
        return mealRecordRepository.findAllByUserIdOrderByConsumedAtDesc(userId);
    }

    public MacroSummary dailySummaryFor(UserId userId, LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        Instant from = target.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = target.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        var meals = mealRecordRepository.findAllByUserIdAndConsumedAtBetweenOrderByConsumedAtDesc(userId, from, to);
        MacroSummary total = new MacroSummary();
        for (var meal : meals) {
            total = total.add(meal.getMacros());
        }
        return total;
    }

    @Transactional
    public Optional<MealRecord> log(LogMealRequest request) {
        var userId = new UserId(request.userId());
        if (!iamGateway.existsUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + request.userId());
        }
        var record = new MealRecord(
                userId,
                MealType.valueOf(request.mealType()),
                request.description(),
                request.macros() == null ? new MacroSummary() : request.macros().toValue(),
                request.consumedAt(),
                request.sourceAnalysisId());
        mealRecordRepository.save(record);
        return Optional.of(record);
    }
}
