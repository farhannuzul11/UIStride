package com.UIStride.repository;

import com.UIStride.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByAccountId(Long accountId);

    List<Activity> findByAccountIdAndStartTimeBetween(Long accountId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT SUM(a.steps) FROM Activity a WHERE a.accountId = :accountId")
    int getTotalStepsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT SUM(a.distance) FROM Activity a WHERE a.accountId = :accountId")
    double getTotalDistanceByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(a.distance), 0) FROM Activity a WHERE a.accountId = :accountId AND a.startTime BETWEEN :startDate AND :endDate")
    double getTotalDistanceByAccountIdAndPeriod(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(a.steps), 0) FROM Activity a WHERE a.accountId = :accountId AND a.startTime BETWEEN :startDate AND :endDate")
    int getTotalStepsByAccountIdAndPeriod(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
