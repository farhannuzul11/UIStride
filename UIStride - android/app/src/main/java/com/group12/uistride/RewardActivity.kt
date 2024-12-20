package com.group12.uistride

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group12.uistride.model.BaseResponse
import com.group12.uistride.model.Reward
import com.group12.uistride.request.BaseApiService
import com.group12.uistride.request.UtilsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RewardActivity : AppCompatActivity() {

    private lateinit var rewardsRecyclerView: RecyclerView
    private lateinit var rewardAdapter: RewardAdapter
    private val rewardsList = mutableListOf<Reward>()

    private lateinit var mApiService: BaseApiService
    private var totalPoints: Int = 0  // Simpan total poin pengguna

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)
        supportActionBar?.hide()

        // Inisialisasi mApiService
        mApiService = UtilsApi.getApiService()

        // Inisialisasi RecyclerView
        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView)

        // Mengambil accountId dari SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val accountId = sharedPreferences.getLong("accountId", -1)

        // Memanggil API untuk mendapatkan total points dan rewards
        if (accountId != -1L) {
            fetchTotalPoints(accountId)
        } else {
            Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT).show()
        }

        fetchRewards(accountId)
        fetchTotalPoints(accountId)
    }

    private fun setupRecyclerView() {
        rewardAdapter = RewardAdapter(rewardsList, { reward ->
            // Logika redeem saat reward dipilih
            if (reward.pointsRequired <= totalPoints) {
                redeemReward(reward.id) // Panggil redeemReward jika poin cukup
            } else {
                Toast.makeText(this, "Not enough points to redeem this reward.", Toast.LENGTH_SHORT).show()
            }
        }, totalPoints) // Tambahkan totalPoints di sini

        rewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        rewardsRecyclerView.adapter = rewardAdapter
    }


    private fun fetchTotalPoints(accountId: Long) {
        mApiService.getStatistics(accountId, "alltime").enqueue(object : Callback<BaseResponse<Map<String, Any>>> {
            override fun onResponse(
                call: Call<BaseResponse<Map<String, Any>>>,
                response: Response<BaseResponse<Map<String, Any>>>
            ) {
                if (response.isSuccessful && response.body()?.payload != null) {
                    val payload = response.body()!!.payload
                    totalPoints = (payload["totalPoints"] as? Number)?.toInt() ?: 0
                    Log.d("RewardActivity", "Total Points: $totalPoints")

                    // Setelah mendapatkan total points, fetch rewards dan setup RecyclerView
                    fetchRewards(accountId)
                } else {
                    Toast.makeText(this@RewardActivity, "Failed to fetch statistics", Toast.LENGTH_SHORT).show()
                    Log.e("RewardActivity", "Response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Map<String, Any>>>, t: Throwable) {
                Toast.makeText(this@RewardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("RewardActivity", "API Error: ${t.localizedMessage}", t)
            }
        })
    }

    private fun fetchRewards(accountId: Long) {
        mApiService.getAllRewards().enqueue(object : Callback<BaseResponse<List<Reward>>> {
            override fun onResponse(
                call: Call<BaseResponse<List<Reward>>>,
                response: Response<BaseResponse<List<Reward>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val rewards = response.body()!!.payload
                    rewardsList.clear()
                    rewardsList.addAll(rewards)

                    // Panggil setupRecyclerView setelah rewards diterima
                    setupRecyclerView()
                } else {
                    Toast.makeText(this@RewardActivity, "Failed to fetch rewards", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<Reward>>>, t: Throwable) {
                Toast.makeText(this@RewardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun redeemReward(rewardId: Long) {
        // Retrieve user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val accountId = sharedPreferences.getLong("accountId", -1)

        if (accountId != -1L) {
            mApiService.redeemReward(accountId, rewardId).enqueue(object : Callback<BaseResponse<String>> {
                override fun onResponse(
                    call: Call<BaseResponse<String>>,
                    response: Response<BaseResponse<String>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val message = response.body()!!.payload
                        Toast.makeText(this@RewardActivity, message, Toast.LENGTH_SHORT).show()
                        // Dapatkan rewards terbaru setelah redeem
                        fetchRewards(accountId)
                    } else {
                        Toast.makeText(this@RewardActivity, "Failed to redeem reward", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String>>, t: Throwable) {
                    Toast.makeText(this@RewardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@RewardActivity, "Invalid account ID", Toast.LENGTH_SHORT).show()
        }
    }
}
