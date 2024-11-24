package com.UIStride.repository;

import com.UIStride.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByAccountId(Long accountId);
}
