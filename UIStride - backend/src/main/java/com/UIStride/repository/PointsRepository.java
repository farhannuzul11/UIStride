package com.UIStride.repository;

import com.UIStride.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
    Optional<Points> findByActivityId(Long activityId);

    boolean existsByActivityId(Long activityId);

    @Query("SELECT COALESCE(SUM(p.pointsAwarded), 0) FROM Points p WHERE p.account.id = :accountId")
    int getTotalPointsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(p.pointsAwarded), 0) FROM Points p WHERE p.account.id = :accountId AND p.activity.startTime BETWEEN :startDate AND :endDate")
    int getTotalPointsByAccountIdAndPeriod(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

