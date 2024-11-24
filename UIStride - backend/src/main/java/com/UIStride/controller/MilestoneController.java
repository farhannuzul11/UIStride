package com.UIStride.controller;

import com.UIStride.model.Milestone;
import com.UIStride.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/milestones")
public class MilestoneController {
    @Autowired
    private MilestoneService milestoneService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<Milestone>>> getAllMilestones() {
        List<Milestone> milestones = milestoneService.getAllMilestones();
        return ResponseEntity.ok(new BaseResponse<>(true, "Milestones fetched successfully", milestones));
    }
}

