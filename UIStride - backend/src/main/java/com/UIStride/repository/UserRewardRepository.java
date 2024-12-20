package com.UIStride.repository;


import com.UIStride.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    @Query("SELECT COALESCE(SUM(r.pointsRequired), 0) FROM UserReward ur JOIN ur.reward r " +
            "WHERE ur.account.id = :accountId " +
            "AND ur.redeemedAt BETWEEN :startDate AND :endDate " +
            "AND ur.status = 'ACTIVE'")
    Integer getTotalPointsRedeemedByAccountIdAndPeriod(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
