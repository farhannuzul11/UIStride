package com.group12.uistride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.group12.uistride.model.Account
import com.group12.uistride.model.BaseResponse
import com.group12.uistride.request.BaseApiService
import com.group12.uistride.request.UtilsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerButton: TextView
    private lateinit var mApiService: BaseApiService
    private lateinit var mContext: Context
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var loginNow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide();

        mContext = this
        mApiService = UtilsApi.getApiService()
        registerButton = findViewById(R.id.button_register)
        username = findViewById(R.id.register_username)
        email = findViewById(R.id.register_email)
        password = findViewById(R.id.register_password)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        loginNow = findViewById(R.id.login_now)

        registerButton.setOnClickListener { handleRegister() }
        loginNow.setOnClickListener { moveActivity(mContext, LoginActivity::class.java) }

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

    private fun handleRegister() {
        if (validateEmail() && validatePassword()) {
            val usernameS = username.text.toString()
            val passwordS = password.text.toString()
            val emailS = email.text.toString()

            mApiService.register(usernameS, passwordS, emailS).enqueue(object : Callback<BaseResponse<Account>> {
                override fun onResponse(
                    call: Call<BaseResponse<Account>>,
                    response: Response<BaseResponse<Account>>
                ) {
                    if (!response.isSuccessful) {
                        Log.e("Register", "Error Code: ${response.code()}, Message: ${response.message()}")
                        Toast.makeText(mContext, "Register failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val res = response.body()
                    if (res?.success == true) {
                        moveActivity(mContext, LoginActivity::class.java)
                    } else {
                        Toast.makeText(mContext, res?.message, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<BaseResponse<Account>>, t: Throwable) {
                    Toast.makeText(mContext, "Register failed", Toast.LENGTH_SHORT).show()
                }
            })
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