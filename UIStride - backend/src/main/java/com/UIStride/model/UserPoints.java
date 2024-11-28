package com.UIStride.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_points")
@Getter
@Setter
public class UserPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private int totalPoints;


    public UserPoints() {
    }

    public UserPoints(Long id, Long accountId, int totalPoints) {
        this.id = id;
        this.accountId = accountId;
        this.totalPoints = totalPoints;
    }
}
