package com.group12.uistride.request;

import com.group12.uistride.model.Account;
import com.group12.uistride.model.Activity;
import com.group12.uistride.model.BaseResponse;
import com.group12.uistride.model.Reward;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiService {

    @POST("account/register")
    Call<BaseResponse<Account>> register (
            @Query("username") String username,
            @Query("password") String password,
            @Query("email") String email
    );

    @POST("account/login")
    Call<BaseResponse<Account>> login (
            @Query("email") String email,
            @Query("password") String password
    );

    @POST("activity/save")
    Call<BaseResponse<Activity>> saveActivity (
            @Query("accountId") Long accountId,
            @Query("distance") double distance,
            @Query("steps") int steps,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("duration") String duration
    );

    @GET("activity/{account_id}")
    Call<BaseResponse<List<Activity>>> getActivityByAccountId(
            @Path("account_id") Long accountId
    );

    @POST("points/process/{accountId}")
    Call<BaseResponse<Void>> processActivityPoints(
            @Path("accountId") Long accountId
    );

    @POST("user-points/update/{accountId}")
    Call<BaseResponse<Void>> updateUserTotalPoints(
            @Path("accountId") Long accountId
    );


    @GET("statistics/total/{accountId}")
    Call<BaseResponse<Map<String, Object>>> getStatistics(
            @Path("accountId") Long accountId,
            @Query("period") String period
    );

    @GET("statistics/grouped/{accountId}")
    Call<BaseResponse<Map<String, Object>>> getGroupedStatistics(
            @Path("accountId") Long accountId,
            @Query("period") String period
    );

    @GET("rewards/all")
    Call<BaseResponse<List<Reward>>> getAllRewards();

    @POST("rewards/redeem/{accountId}/{rewardId}")
    Call<BaseResponse<String>> redeemReward(
            @Path("accountId") Long accountId,
            @Path("rewardId") Long rewardId
    );

    @POST("rewards/cancel/{accountId}/{userRewardId}")
    Call<BaseResponse<String>> undoRedeemReward(
            @Path("accountId") Long accountId,
            @Path("userRewardId") Long userRewardId
    );

}