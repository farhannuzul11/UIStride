package com.UIStride.repository;

import com.UIStride.MilestoneType;
import com.UIStride.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByType(MilestoneType type); // Ganti tipe parameter dengan MilestoneType
}
