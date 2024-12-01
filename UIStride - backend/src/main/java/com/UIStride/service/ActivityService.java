package com.UIStride.service;

import com.UIStride.model.Activity;
import com.UIStride.repository.ActivityRepository;
import com.UIStride.repository.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PointsRepository pointsRepository;

    public List<Activity> getActivitiesByAccountId(Long accountId) {
        return activityRepository.findByAccountId(accountId);
    }

    // Menambahkan method untuk findByAccountIdAndStartTimeBetween
    public List<Activity> getActivitiesByAccountIdAndPeriod(Long accountId, LocalDateTime startTime, LocalDateTime endTime) {
        return activityRepository.findByAccountIdAndStartTimeBetween(accountId, startTime, endTime);
    }

    // Menambahkan method untuk getTotalStepsByAccountId
    public int getTotalStepsByAccountId(Long accountId) {
        return activityRepository.getTotalStepsByAccountId(accountId);
    }

    // Menambahkan method untuk getTotalDistanceByAccountId
    public double getTotalDistanceByAccountId(Long accountId) {
        return activityRepository.getTotalDistanceByAccountId(accountId);
    }

    // Statistik yang sudah ada berdasarkan periode (daily, weekly, monthly, alltime)
    public Map<String, Object> getUserStatistics(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        double totalDistance = getTotalDistanceByAccountIdAndPeriod(accountId, startDate, endDate);
        int totalSteps = getTotalStepsByAccountIdAndPeriod(accountId, startDate, endDate);
        int totalPoints = pointsRepository.getTotalPointsByAccountIdAndPeriod(accountId, startDate, endDate);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalDistance", totalDistance);
        statistics.put("totalSteps", totalSteps);
        statistics.put("totalPoints", totalPoints);
        return statistics;
    }

    // Mendapatkan total jarak berdasarkan AccountId dalam periode waktu tertentu
    public double getTotalDistanceByAccountIdAndPeriod(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.getTotalDistanceByAccountIdAndPeriod(accountId, startDate, endDate);
    }

    // Mendapatkan total langkah berdasarkan AccountId dalam periode waktu tertentu
    public int getTotalStepsByAccountIdAndPeriod(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.getTotalStepsByAccountIdAndPeriod(accountId, startDate, endDate);
    }

    // Menambahkan aktivitas baru
    public Activity addActivity(Long accountId, double distance, int steps, LocalDateTime start, LocalDateTime end, String duration) {
        System.out.println("Service - AccountId: " + accountId);
        System.out.println("Service - Distance: " + distance);
        System.out.println("Service - Steps: " + steps);
        System.out.println("Service - StartTime: " + start);
        System.out.println("Service - EndTime: " + end);
        System.out.println("Service - Duration: " + duration);

        try {
            Activity activity = new Activity();
            activity.setAccountId(accountId);
            activity.setDistance(distance);
            activity.setSteps(steps);
            activity.setStartTime(start);
            activity.setEndTime(end);
            activity.setDuration(duration);

            Activity savedActivity = activityRepository.save(activity);
            System.out.println("Repository returned saved Activity: " + savedActivity);
            return savedActivity;
        } catch (Exception e) {
            System.err.println("Error in service: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
