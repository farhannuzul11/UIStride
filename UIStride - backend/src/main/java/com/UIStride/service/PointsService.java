package com.UIStride.service;

import com.UIStride.model.Activity;
import com.UIStride.model.Milestone;
import com.UIStride.model.Points;
import com.UIStride.model.UserPoints;
import com.UIStride.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsService {

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private UserPointsRepository userPointsRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccountRepository accountRepository;

    public int getTotalPoints(Long accountId) {
        return pointsRepository.getTotalPointsByAccountId(accountId);
    }

    @Transactional
    public void processExistingActivitiesForPoints(Long accountId) {
        List<Activity> activities = activityRepository.findByAccountId(accountId);

        for (Activity activity : activities) {
            if (!pointsRepository.existsByActivityId(activity.getId())) {
                int points = calculatePoints(activity);

                Points pointsEntity = new Points();
                pointsEntity.setAccount(activity.getAccount());
                pointsEntity.setActivity(activity);
                pointsEntity.setPointsAwarded(points);
                pointsEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

                pointsRepository.save(pointsEntity);
            }
        }
    }

   /* @Transactional
    public void updateUserPoints(Long accountId, int points) {
        // Ambil entitas UserPoints berdasarkan accountId
        UserPoints userPoints = userPointsRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("User points not found for account ID: " + accountId));

        // Update total points
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        userPoints.setUpdatedAt(LocalDateTime.now());  // Jangan lupa update timestamp
        userPointsRepository.save(userPoints);
    }*/


    private int calculatePoints(Activity activity) {
        int distancePoints = (int) (activity.getDistance()) * 10; // 10 points per km
        int stepPoints = (activity.getSteps() / 10) * 5; // 5 points per 1000 steps

        System.out.println("Distance: " + activity.getDistance() + " km, Points: " + distancePoints);
        System.out.println("Steps: " + activity.getSteps() + ", Points: " + stepPoints);

        return distancePoints + stepPoints;
    }
}

