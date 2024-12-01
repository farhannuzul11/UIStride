package com.group12.uistride

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.group12.uistride.model.Activity
import com.group12.uistride.model.BaseResponse
import com.group12.uistride.request.BaseApiService
import com.group12.uistride.request.RetrofitClient
import com.group12.uistride.request.UtilsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private val activityList = mutableListOf<Activity>()
    private lateinit var totalDistanceTextView: TextView
    private lateinit var totalStepsTextView: TextView
    private lateinit var totalPointsTextView: TextView
    private lateinit var statsPeriodSpinner: Spinner
    private lateinit var mApiService: BaseApiService

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val accountId = getAccountIdFromPreferences()
        Log.d(TAG, "Fetched accountId from preferences: $accountId")

        if (accountId == -1L) {
            Log.w(TAG, "No accountId found, redirecting to LoginActivity")
            Toast.makeText(this, "User not logged in. Please login first.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        } else {
            Log.d(TAG, "User logged in as accountId: $accountId")
            Toast.makeText(this, "Logged in as User ID: $accountId", Toast.LENGTH_SHORT).show()
        }

        mApiService = UtilsApi.getApiService()


        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        usernameTextView.text = "Hello, $username"

        // Inisialisasi TextView Statistik
        totalDistanceTextView = findViewById(R.id.totalDistanceTextView)
        totalStepsTextView = findViewById(R.id.totalStepsTextView)
        totalPointsTextView = findViewById(R.id.totalPointsTextView)

        // Inisialisasi Spinner
        statsPeriodSpinner = findViewById(R.id.statsPeriodSpinner)
        statsPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPeriod = when (position) {
                    0 -> "daily"
                    1 -> "weekly"
                    2 -> "monthly"
                    3 -> "yearly"
                    else -> "alltime"
                }
                fetchStatistics(accountId, selectedPeriod)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set default statistik
        fetchStatistics(accountId, "alltime")


        recyclerView = findViewById(R.id.activityRecyclerView)
        activityAdapter = ActivityAdapter(activityList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = activityAdapter

        fetchActivities(accountId)


        // Initialize and set up the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Handle Home action
                    true
                }
                R.id.nav_record -> {
                    val intent = Intent(this, RecordActivity::class.java)
                    recordActivityLauncher.launch(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Handle Profile action
                    val intent = Intent(this, StatisticsActivity::class.java)
                    intent.putExtra("accountId", accountId)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    private val recordActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val distance = data.getDoubleExtra("distance", 0.0)
                val steps = data.getIntExtra("steps", 0)
                val startTime = data.getLongExtra("startTime", 0)
                val endTime = data.getLongExtra("endTime", 0)
                val duration = data.getStringExtra("duration")

                // Format waktu ke ISO 8601 (yyyy-MM-dd'T'HH:mm:ss)
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formattedStartTime = formatter.format(Date(startTime))
                val formattedEndTime = formatter.format(Date(endTime))

                // Pastikan accountId sudah ada (dapatkan dari preferensi atau sumber lain)
                val accountId: Long = getAccountIdFromPreferences()
                if (accountId == -1L) {
                    Log.e(TAG, "Account ID not found, cannot create Activity object.")
                    return@registerForActivityResult
                }

                // Buat objek Activity dari kelas Java
                val newActivity = Activity(
                    accountId,             // accountId dari preferensi
                    distance,              // Jarak
                    steps,                 // Langkah
                    formattedStartTime,    // Waktu mulai dalam format ISO
                    formattedEndTime,      // Waktu selesai dalam format ISO
                    duration ?: ""         // Durasi aktivitas
                )

                // Tambahkan ke list dan perbarui adapter
                activityList.add(0, newActivity) // Tambahkan di awal list
                activityAdapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0) // Scroll ke aktivitas baru
            }
        }
    }



    private fun getAccountIdFromPreferences(): Long {
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        return sharedPreferences.getLong("accountId", -1)
    }

    private fun fetchStatistics(accountId: Long, period: String) {
        mApiService.getStatistics(accountId, period).enqueue(object : Callback<BaseResponse<Map<String, Any>>> {
            override fun onResponse(
                call: Call<BaseResponse<Map<String, Any>>>,
                response: Response<BaseResponse<Map<String, Any>>>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val statistics = response.body()!!.payload

                    // Ambil data dari payload dengan format yang sesuai
                    val totalDistance = (statistics["totalDistance"] as? Double) ?: 0.0
                    val totalSteps = (statistics["totalSteps"] as? Double)?.toInt() ?: 0
                    val totalPoints = (statistics["totalPoints"] as? Double)?.toInt() ?: 0

                    // Format totalDistance dengan 2 angka desimal
                    val formattedDistance = String.format(Locale.getDefault(), "%.2f", totalDistance)

                    // Update TextViews
                    totalDistanceTextView.text = "Total Distance: $formattedDistance km"
                    totalStepsTextView.text = "Total Steps: $totalSteps"
                    totalPointsTextView.text = "Total Points: $totalPoints"
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load statistics", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<Map<String, Any>>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchActivities(accountId: Long) {
        mApiService.getActivityByAccountId(accountId).enqueue(object : Callback<BaseResponse<List<Activity>>> {
            override fun onResponse(
                call: Call<BaseResponse<List<Activity>>>,
                response: Response<BaseResponse<List<Activity>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val activities = response.body()?.payload
                    if (activities != null) {
                        activityList.clear()
                        activityList.addAll(activities.reversed())
                        activityAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e(TAG, "Failed to fetch activities: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Failed to fetch activities", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<Activity>>>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Error fetching activities", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
