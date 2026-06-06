package com.bodymatch.nutrition.meal;

import com.bodymatch.nutrition.shared.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {
    List<MealRecord> findAllByUserIdAndConsumedAtBetweenOrderByConsumedAtDesc(UserId userId, Instant from, Instant to);
    List<MealRecord> findAllByUserIdOrderByConsumedAtDesc(UserId userId);
}
