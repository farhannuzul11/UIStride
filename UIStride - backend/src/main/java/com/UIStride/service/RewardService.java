package com.UIStride.service;

import com.UIStride.UserRewardStatus;
import com.UIStride.model.Account;
import com.UIStride.model.Reward;
import com.UIStride.model.UserPoints;
import com.UIStride.model.UserReward;
import com.UIStride.repository.AccountRepository;
import com.UIStride.repository.RewardRepository;
import com.UIStride.repository.UserPointsRepository;
import com.UIStride.repository.UserRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    @Autowired
    private UserPointsRepository userPointsRepository;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private UserRewardRepository userRewardRepository;

    @Autowired
    private AccountRepository accountRepository; // Menambahkan repository untuk Account

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    public String redeemReward(Long accountId, Long rewardId) {
        // Mencari reward berdasarkan ID
        Optional<Reward> rewardOpt = rewardRepository.findById(rewardId);
        if (rewardOpt.isEmpty()) {
            return "Reward not found.";
        }

        Reward reward = rewardOpt.get();

        // Mencari user berdasarkan accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        // Mencari user points berdasarkan account
        UserPoints userPoints = userPointsRepository.findByAccount(account);
        if (userPoints == null) {
            return "User points record not found.";
        }

        // Memeriksa jika user memiliki cukup poin
        if (userPoints.getTotalPoints() < reward.getPointsRequired()) {
            return "Insufficient points.";
        }

        // Mengurangi poin user sesuai dengan points_required reward
        userPoints.setTotalPoints(userPoints.getTotalPoints() - reward.getPointsRequired());
        userPointsRepository.save(userPoints);  // Simpan perubahan poin

        // Memeriksa ketersediaan reward
        if (reward.getQuantity() <= 0) {
            return "Reward out of stock.";
        }

        // Mengurangi kuantitas reward
        reward.setQuantity(reward.getQuantity() - 1);
        rewardRepository.save(reward);

        // Save user reward
        UserReward userReward = new UserReward();
        userReward.setAccount(account);
        userReward.setReward(reward);
        userReward.setRedeemedAt(LocalDateTime.now());
        userReward.setStatus(UserRewardStatus.REDEEMED);  // Status redeemed
        userRewardRepository.save(userReward);

        return "Reward redeemed successfully.";
    }

    public String undoRedeem(Long accountId, Long userRewardId) {
        // Mencari user reward berdasarkan ID
        Optional<UserReward> userRewardOpt = userRewardRepository.findById(userRewardId);
        if (userRewardOpt.isEmpty()) {
            return "User reward not found.";
        }

        UserReward userReward = userRewardOpt.get();

        // Memeriksa status reward
        if (userReward.getStatus() == UserRewardStatus.CANCELED) {
            return "This reward has already been undone.";
        }

        // Mencari reward terkait user reward
        Reward reward = userReward.getReward();
        if (reward == null) {
            return "Reward not found.";
        }

        // Mencari user points berdasarkan accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        UserPoints userPoints = userPointsRepository.findByAccount(account);
        if (userPoints == null) {
            return "User points record not found.";
        }

        // Undo redeem: add points back and update reward quantity
        userPoints.setTotalPoints(userPoints.getTotalPoints() + reward.getPointsRequired());
        reward.setQuantity(reward.getQuantity() + 1);
        userPointsRepository.save(userPoints);
        rewardRepository.save(reward);

        // Update user reward status
        userReward.setStatus(UserRewardStatus.CANCELED); // Menggunakan enum CANCELED
        userRewardRepository.save(userReward);

        return "Redeem undone successfully.";
    }
}
