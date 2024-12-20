package com.UIStride.controller;

import com.UIStride.model.UserPoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.UIStride.service.UserPointsService;


@RestController
@RequestMapping("/user-points")
public class UserPointsController {

    @Autowired
    private UserPointsService userPointsService;

    @PostMapping("/update/{accountId}")
    public BaseResponse<UserPoints> updateUserPoints(@PathVariable Long accountId) {
        try {
            UserPoints updatedPoints = userPointsService.updateUserPoints(accountId);
            return new BaseResponse<>(true, "User points updated successfully", updatedPoints);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Error updating points: " + e.getMessage(), null);
        }
    }
}
