package com.UIStride.service;

import com.UIStride.model.Activity;
import com.UIStride.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getActivitiesByAccountId(Long accountId) {
        return activityRepository.findByAccountId(accountId);
    }
    public Activity getActivity(Long accountId) {
        List<Activity> activities = activityRepository.findByAccountId(accountId);

        if (activities.isEmpty()) {
            Activity newRecord = new Activity();
            newRecord.setAccountId(accountId);
            return activityRepository.save(newRecord);
        }

        return activities.get(0);
    }

    public Activity updateActivity(Long accountId, Double distance, Integer steps) {
        Activity activity = getActivity(accountId);

        activity.setDistance(distance);
        activity.setSteps(steps);
        activity.setEndTime(LocalDateTime.now());

        return activityRepository.save(activity);
    }


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
