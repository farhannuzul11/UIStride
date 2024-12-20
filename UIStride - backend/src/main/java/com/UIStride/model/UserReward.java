package com.UIStride.model;

import com.UIStride.UserRewardStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_rewards")
@Getter
@Setter
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRewardStatus status;

}