package com.UIStride.controller;

import com.UIStride.model.UserPoints;
import com.UIStride.service.UserPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-points")
public class UserPointsController {
    @Autowired
    private UserPointsService userPointsService;

    @GetMapping("/{accountId}")
    public ResponseEntity<BaseResponse<UserPoints>> getUserPoints(@PathVariable Long accountId) {
        UserPoints userPoints = userPointsService.getUserPoints(accountId);
        return ResponseEntity.ok(new BaseResponse<>(true, "User points fetched successfully", userPoints));
    }

    @PostMapping("/check-reward")
    public ResponseEntity<BaseResponse<String>> checkAndReward(
            @RequestParam Long accountId,
            @RequestParam double distance,
            @RequestParam int steps) {
        userPointsService.checkAndRewardMilestones(accountId, distance, steps);
        return ResponseEntity.ok(new BaseResponse<>(true, "Milestones checked and points rewarded", null));
    }
}

