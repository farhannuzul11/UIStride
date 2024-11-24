package com.UIStride.service;

import com.UIStride.MilestoneType;
import com.UIStride.model.Milestone;
import com.UIStride.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository milestoneRepository;

    public List<Milestone> getAllMilestones() {
        return milestoneRepository.findAll();
    }

    public List<Milestone> getMilestonesByType(MilestoneType type) {
        return milestoneRepository.findByType(type);
    }
}
