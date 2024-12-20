package com.group12.uistride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.group12.uistride.model.Account
import com.group12.uistride.model.BaseResponse
import com.group12.uistride.request.BaseApiService
import com.group12.uistride.request.UtilsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var registerNow: TextView
    private lateinit var loginButton: Button
    private var loggedAccount: Account? = null
    private lateinit var mApiService: BaseApiService
    private lateinit var mContext: Context
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_UIStride)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide();

        // Cek apakah user sudah login
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val accountId = sharedPreferences.getLong("accountId", -1)

        if (accountId != -1L) {
            Log.d("LoginActivity", "User already logged in with accountId: $accountId")
            // User sudah login, langsung pindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        mContext = this
        mApiService = UtilsApi.getApiService()
        registerNow = findViewById(R.id.register_now)
        loginButton = findViewById(R.id.login_button)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)

        loginButton.setOnClickListener { handleLogin() }
        registerNow.setOnClickListener { moveActivity(this, RegisterActivity::class.java) }

        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun moveActivity(ctx: Context, cls: Class<*>) {
        val intent = Intent(ctx, cls)
        startActivity(intent)
    }

    private fun handleLogin() {
        if (validateEmail() && validatePassword()) {
            val emailS = email.text.toString()
            val passwordS = password.text.toString()

            mApiService.login(emailS, passwordS).enqueue(object : Callback<BaseResponse<Account>> {
                override fun onResponse(
                    call: Call<BaseResponse<Account>>,
                    response: Response<BaseResponse<Account>>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(mContext, "Application error ${response.code()}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val res = response.body()
                    if (res?.success == true) {
                        loggedAccount = res.payload

                        // Simpan accountId, username, dan email ke SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putLong("accountId", loggedAccount?.id ?: -1) // Simpan accountId
                        editor.putString("username", loggedAccount?.username ?: "User") // Simpan username
                        editor.putString("email", emailS) // Simpan email
                        editor.apply()

                        val intent = Intent(mContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(mContext, "Welcome ${loggedAccount?.username}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(mContext, res?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Account>>, t: Throwable) {
                    Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveAccountIdToPreferences(accountId: Long?) {
        if (accountId != null) {
            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("accountId", accountId)
            editor.apply()
        }
    }

    private fun validateEmail(): Boolean {
        val emailText = email.text.toString()
        val emailPattern = Regex("^[a-zA-Z0-9]+@[a-zA-Z_]+?\\.[a-zA-Z.]+[a-zA-Z]+$")

        return if (emailText.isEmpty()) {
            emailInputLayout.error = "Email cannot be empty"
            false
        } else if (!emailPattern.matches(emailText)) {
            emailInputLayout.error = "Invalid email format"
            false
        } else {
            emailInputLayout.error = null
            true
        }
    }


    private fun validatePassword(): Boolean {
        val passwordText = password.text.toString()
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$")

        return if (passwordText.isEmpty()) {
            passwordInputLayout.error = "Password cannot be empty"
            false
        } else if (!passwordPattern.matches(passwordText)) {
            passwordInputLayout.error = "Password must contain at least 8 characters, including uppercase, lowercase, and a number"
            false
        } else {
            passwordInputLayout.error = null
            true
        }
    }

}


