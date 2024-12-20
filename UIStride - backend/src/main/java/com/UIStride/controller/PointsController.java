package com.UIStride.controller;

import com.UIStride.model.Points;
import com.UIStride.repository.PointsRepository;
import com.UIStride.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/total/{accountId}")
    public ResponseEntity<BaseResponse<Integer>> getTotalPoints(@PathVariable Long accountId) {
        try {
            int totalPoints = pointsService.getTotalPoints(accountId);
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Total points retrieved successfully.",
                    totalPoints));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(
                    false,
                    "An error occurred while fetching total points: " + e.getMessage(),
                    null));
        }
    }

    @PostMapping("/process/{accountId}")
    public ResponseEntity<BaseResponse<Void>> processExistingActivitiesForPoints(
            @PathVariable Long accountId) {
        try {
            pointsService.processExistingActivitiesForPoints(accountId);
            return ResponseEntity.ok(
                    new BaseResponse<>(true, "Points processed successfully for existing activities.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed to process points: " + e.getMessage(), null));
        }
    }
}


