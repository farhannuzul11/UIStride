package com.UIStride.repository;

import com.UIStride.model.Account;
import com.UIStride.model.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    UserPoints findByAccount(Account account);

    @Query("SELECT up.totalPoints FROM UserPoints up WHERE up.account.id = :accountId")
    Integer findTotalPointsByAccountId(@Param("accountId") Long accountId);

}

