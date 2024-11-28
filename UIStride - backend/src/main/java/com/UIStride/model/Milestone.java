package com.UIStride.model;

import com.UIStride.MilestoneType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Milestone")
@Getter
@Setter
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double requiredDistance;
    private int requiredSteps;
    private int points;
    private MilestoneType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Milestone(Long id, String name, double requiredDistance, int requiredSteps, int points, MilestoneType type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.requiredDistance = requiredDistance;
        this.requiredSteps = requiredSteps;
        this.points = points;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
