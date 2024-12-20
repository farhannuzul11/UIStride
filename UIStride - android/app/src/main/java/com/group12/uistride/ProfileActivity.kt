package com.group12.uistride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()

        // Initialize UI components
        val initialTextView: TextView = findViewById(R.id.initial)
        val usernameTextView: TextView = findViewById(R.id.username)
        val emailTextView: TextView = findViewById(R.id.email)
        val logoutButton: Button = findViewById(R.id.btn_logout)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        val statisticsPreview: LinearLayout = findViewById(R.id.statisticsPreview)
        val rewardsPreview: LinearLayout = findViewById(R.id.rewardsPreview)

        // Retrieve user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val accountId = sharedPreferences.getLong("accountId", -1)
        val username = sharedPreferences.getString("username", "Unknown")
        val email = sharedPreferences.getString("email", "user@example.com")
        val points = sharedPreferences.getInt("points", 0)

        // Log for debugging
        Log.d("ProfileActivity", "Loaded data: accountId=$accountId, username=$username, email=$email, points=$points")

        // Update UI components
        usernameTextView.text = username
        emailTextView.text = email

        // Set initial character (e.g., the first letter of the username)
        val initial = username?.firstOrNull()?.uppercaseChar() ?: 'P'
        initialTextView.text = initial.toString()

        // Handle logout button click
        logoutButton.setOnClickListener {
            // Show confirmation dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")

            // Set positive button (Yes)
            builder.setPositiveButton("Yes") { _, _ ->
                Log.d("ProfileActivity", "Logging out user with accountId = $accountId")

                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                // Verify data is cleared
                val clearedAccountId = sharedPreferences.getLong("accountId", -1)
                Log.d("ProfileActivity", "After logout: accountId = $clearedAccountId")

                // Redirect to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            // Set negative button (No)
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close dialog
            }

            // Show the dialog
            val dialog = builder.create()
            dialog.show()
        }

        // Handle BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_record -> {
                    val intent = Intent(this, RecordActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }

        rewardsPreview.setOnClickListener {
            val intent = Intent(this, RewardActivity::class.java)
            intent.putExtra("accountId", accountId)
            startActivity(intent)
        }

        // Handle Statistics Preview click to navigate to StatisticsActivity
        statisticsPreview.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("accountId", accountId)
            startActivity(intent)
        }
    }
}
