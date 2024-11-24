package com.UIStride.service;

import com.UIStride.model.Milestone;
import com.UIStride.model.UserPoints;
import com.UIStride.repository.MilestoneRepository;
import com.UIStride.repository.UserPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPointsService {
    @Autowired
    private UserPointsRepository userPointsRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    public UserPoints getUserPoints(Long accountId) {
        return userPointsRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    UserPoints newUserPoints = new UserPoints();
                    newUserPoints.setAccountId(accountId);
                    newUserPoints.setTotalPoints(0);
                    return userPointsRepository.save(newUserPoints);
                });
    }

    public void addPoints(Long accountId, int points) {
        UserPoints userPoints = getUserPoints(accountId);
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        userPointsRepository.save(userPoints);
    }

    public void checkAndRewardMilestones(Long accountId, double distance, int steps) {
        List<Milestone> milestones = milestoneRepository.findAll();

        for (Milestone milestone : milestones) {
            boolean isAchieved = false;

            switch (milestone.getType()) {
                case DISTANCE:
                    isAchieved = milestone.getRequiredDistance() <= distance;
                    break;
                case STEP_COUNT:
                    isAchieved = milestone.getRequiredSteps() <= steps;
                    break;
                case COMBINED:
                    isAchieved = milestone.getRequiredDistance() <= distance && milestone.getRequiredSteps() <= steps;
                    break;
            }

            if (isAchieved) {
                addPoints(accountId, milestone.getPoints());
            }
        }
    }
}
