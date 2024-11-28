package com.group12.uistride.model;

import java.io.Serializable;

public class Activity implements Serializable {
    private Long accountId;
    private double distance;
    private int steps;
    private String startTime;
    private String endTime;
    private String duration;

    public Activity(Long accountId, double distance, int steps, String startTime, String endTime, String duration) {
        this.accountId = accountId;
        this.distance = distance;
        this.steps = steps;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getDuration() {
        return duration;
    }
}
