package com.UIStride.controller;

import com.UIStride.model.Activity;
import com.UIStride.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<Activity>> saveActivity(
            @RequestParam Long accountId,
            @RequestParam double distance,
            @RequestParam int steps,
            @RequestParam String startTime, // ISO-8601 formatted string
            @RequestParam String endTime   // ISO-8601 formatted string
    ) {
        // Log parameter yang diterima
        System.out.println("Received parameters:");
        System.out.println("AccountId: " + accountId);
        System.out.println("Distance: " + distance);
        System.out.println("Steps: " + steps);
        System.out.println("StartTime: " + startTime);
        System.out.println("EndTime: " + endTime);

        // Parse the startTime and endTime to LocalDateTime
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            start = LocalDateTime.parse(startTime);
            end = LocalDateTime.parse(endTime);
            System.out.println("Parsed StartTime: " + start);
            System.out.println("Parsed EndTime: " + end);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return new ResponseEntity<>(new BaseResponse<>(false, "Invalid time format", null), HttpStatus.BAD_REQUEST);
        }

        // Calculate the duration
        String duration = calculateDuration(start, end);
        System.out.println("Calculated Duration: " + duration);

        // Save the activity
        Activity savedActivity = null;
        try {
            savedActivity = activityService.addActivity(accountId, distance, steps, start, end, duration);
            System.out.println("Saved Activity: " + savedActivity);
        } catch (Exception e) {
            System.err.println("Error saving activity: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new BaseResponse<>(false, "Error saving activity", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Return the response
        if (savedActivity != null) {
            return new ResponseEntity<>(new BaseResponse<>(true, "Activity saved successfully", savedActivity), HttpStatus.CREATED);
        } else {
            System.out.println("Activity not saved, returning BAD_REQUEST");
            return new ResponseEntity<>(new BaseResponse<>(false, "Failed to save activity", null), HttpStatus.BAD_REQUEST);
        }
    }

    // Helper method to calculate duration in HH:mm:ss format
    private String calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        long durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        long hours = durationSeconds / 3600;
        long minutes = (durationSeconds % 3600) / 60;
        long seconds = durationSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @GetMapping("/{account_id}")
    public ResponseEntity<BaseResponse<List<Activity>>> getActivitiesByAccountId(
            @PathVariable Long account_id
    ) {
        List<Activity> activities = activityService.getActivitiesByAccountId(account_id);

        if (activities.isEmpty()) {
            return new ResponseEntity<>(new BaseResponse<>(false, "No activities found for the given accountId", null), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new BaseResponse<>(true, "Activities fetched successfully", activities), HttpStatus.OK);
        }
    }

}
