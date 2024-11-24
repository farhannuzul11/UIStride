package com.UIStride.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name ="Activity")
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "distance")
    private double distance = 0.0;

    @Column(name = "steps")
    private int steps = 0;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private String duration;

    public Activity() {
    }


    public Activity(Long accountId, double distance, int steps, LocalDateTime startTime, LocalDateTime endTime, String duration) {
        this.accountId = accountId;
        this.distance = distance;
        this.steps = steps;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }
}
