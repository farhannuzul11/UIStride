package com.group12.uistride

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var bottomNavigationView: BottomNavigationView

    // Sample data for the RecyclerView
    private val activityList = listOf(
        RecentActivity("October 09", "10.12 km", "2 h 12 m", "11.2 km/hr"),
        RecentActivity("October 05", "9.89 km", "1 h 40 m", "10.8 km/hr"),
        RecentActivity("October 01", "9.12 km", "1 h 25 m", "10 km/hr")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize and set up the RecyclerView
        recyclerView = findViewById(R.id.recentActivityRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecentActivityAdapter(activityList)

        // Initialize and set up the ProgressBar
     //   progressBar = findViewById(R.id.progressBar)
     //   progressBar.progress = 70 // Set progress based on your logic (example value)

        // Initialize and set up the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Handle Home action
                    true
                }
                R.id.nav_record -> {
                    // Handle Activity action
                    val intent = Intent(this, RecordActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Handle Profile action
                    true
                }
                else -> false
            }
        }
    }
}
