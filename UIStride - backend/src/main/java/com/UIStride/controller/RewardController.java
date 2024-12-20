package com.UIStride.controller;

import com.UIStride.model.Reward;
import com.UIStride.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<Reward>>> getAllRewards() {
        List<Reward> rewards = rewardService.getAllRewards();
        BaseResponse<List<Reward>> response = new BaseResponse<>(true, "Rewards retrieved successfully.", rewards);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint untuk redeem reward
    @PostMapping("/redeem/{accountId}/{rewardId}")
    public ResponseEntity<BaseResponse<String>> redeemReward(
            @PathVariable Long accountId,
            @PathVariable Long rewardId) {
        String result = rewardService.redeemReward(accountId, rewardId);

        BaseResponse<String> response = new BaseResponse<>(
                result.equals("Reward redeemed successfully."),
                result,
                result
        );

        // Menentukan HTTP Status berdasarkan keberhasilan redeem
        return result.equals("Reward redeemed successfully.") ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Endpoint untuk undo redeem reward
    @PostMapping("/cancel/{accountId}/{userRewardId}")
    public ResponseEntity<BaseResponse<String>> undoRedeem(
            @PathVariable Long accountId,
            @PathVariable Long userRewardId) {
        String result = rewardService.undoRedeem(accountId, userRewardId);

        BaseResponse<String> response = new BaseResponse<>(
                result.equals("Redeem canceled successfully."),
                result,
                result
        );

        // Menentukan HTTP Status berdasarkan keberhasilan undo redeem
        return result.equals("Redeem canceled successfully.") ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
