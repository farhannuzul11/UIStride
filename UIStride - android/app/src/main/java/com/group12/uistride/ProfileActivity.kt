package com.group12.uistride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val logoutButton: Button = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val accountId = sharedPreferences.getLong("accountId", -1)

            Log.d("ProfileActivity", "Before logout: accountId = $accountId")

            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val clearedAccountId = sharedPreferences.getLong("accountId", -1)
            Log.d("ProfileActivity", "After logout: accountId = $clearedAccountId")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }



    }

}