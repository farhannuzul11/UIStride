package com.UIStride.controller;

import com.UIStride.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/total/{accountId}")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getStatistics(
            @PathVariable Long accountId,
            @RequestParam String period) {

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate;
            LocalDateTime endDate = now;

            switch (period.toLowerCase()) {
                case "daily":
                    startDate = now.minusDays(1);
                    break;
                case "weekly":
                    startDate = now.minusWeeks(1);
                    break;
                case "monthly":
                    startDate = now.minusMonths(1);
                    break;
                case "yearly":
                    startDate = now.minusYears(1);
                    break;
                case "alltime":
                    startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
                    break;
                default:
                    return ResponseEntity.badRequest().body(new BaseResponse<>(
                            false,
                            "Invalid period. Use daily, weekly, monthly, yearly, or alltime.",
                            null));
            }

            Map<String, Object> statistics = statisticsService.getUserStatistics(accountId, startDate, endDate);
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Statistics retrieved successfully.",
                    statistics));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(
                    false,
                    "An error occurred while fetching statistics: " + e.getMessage(),
                    null));
        }
    }

    @GetMapping("/grouped/{accountId}")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getGroupedStatistics(
            @PathVariable Long accountId,
            @RequestParam String period) {

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate;
            LocalDateTime endDate = now;

            switch (period.toLowerCase()) {
                case "daily":
                    startDate = now.minusDays(7);  // last 7 days
                    break;
                case "weekly":
                    startDate = now.minusWeeks(4); // last 4 weeks
                    break;
                case "monthly":
                    startDate = now.minusMonths(6); // last 6 months
                    break;
                case "yearly":
                    startDate = now.minusYears(1);  // last year
                    break;
                case "alltime":
                    startDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);  // from 1970
                    break;
                default:
                    return ResponseEntity.badRequest().body(new BaseResponse<>(
                            false,
                            "Invalid period. Use daily, weekly, monthly, yearly, or alltime.",
                            null));
            }

            Map<String, Object> statistics = statisticsService.getGroupedStatistics(accountId, startDate, endDate, period);
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Statistics retrieved successfully.",
                    statistics));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(
                    false,
                    "An error occurred while fetching statistics: " + e.getMessage(),
                    null));
        }
    }
}
