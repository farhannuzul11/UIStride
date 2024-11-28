package com.group12.uistride

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private val activityList = mutableListOf<Activity>()

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

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        usernameTextView.text = "Hello, $username"


        recyclerView = findViewById(R.id.activityRecyclerView)
        activityAdapter = ActivityAdapter(activityList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = activityAdapter

        mApiService = UtilsApi.getApiService()

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
                    val intent = Intent(this, ProfileActivity::class.java)
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

                // Konversi waktu dari Long ke String
                val formattedStartTime = DateFormat.getDateTimeInstance().format(Date(startTime))
                val formattedEndTime = DateFormat.getDateTimeInstance().format(Date(endTime))

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
                    formattedStartTime,    // Waktu mulai dalam format String
                    formattedEndTime,      // Waktu selesai dalam format String
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
