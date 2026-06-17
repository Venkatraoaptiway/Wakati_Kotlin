package com.example.wakati_kotlin.customer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityLoginBinding
import com.example.wakati_kotlin.utils.GlobalToast
import com.example.wakati_kotlin.utils.ToastType
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBar(this, true)

        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)

        binding.buttonLogin.setOnClickListener {

            if (!validateData()) {
                return@setOnClickListener
            }

            val jsonObject = JsonObject().apply {
                addProperty(
                    "mobileNumber", "269${binding.editTextPhone.text.toString()}"
                )
                addProperty("Password", "dev@wakati#2024")
                addProperty("force_logout", true)
            }

            observeLogin(jsonObject)
        }

        binding.textRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }

        binding.textForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    private fun observeLogin(jsonObject: JsonObject) {

        LoaderUtils.showLoader(this)

        val retrofitClass = RetrofitClass.getRetrofit()

        retrofitClass.loginAuth(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                LoaderUtils.hideLoader()

                if (response.isSuccessful && response.body() != null) {

                    val loginResponse = response.body()!!
                    val userId = loginResponse.response?.userId
                    val fullName = loginResponse.response?.fullName

                    sharedPreferences.edit().apply {
                        clear()
                        apply()
                    }

                    GlobalToast.show(this@LoginActivity,"Login Successfull",ToastType.SUCCESS);

                    val intent = Intent(this@LoginActivity, LoginPinActivity::class.java)

                    intent.putExtra("phone_number", binding.editTextPhone.text.toString())
                    intent.putExtra("user_id", userId)
                    intent.putExtra("fullName", fullName)

                    startActivity(intent)

//                    Toast.makeText(this@LoginActivity, "Login success", Toast.LENGTH_SHORT).show()

                } else {
//                    Toast.makeText(this@LoginActivity, "Login failed ${response.message()}", Toast.LENGTH_SHORT).show()

                    GlobalToast.show(this@LoginActivity,response.message(),ToastType.ERROR);
                }
            }

            override fun onFailure(
                call: Call<LoginResponse>, t: Throwable
            ) {

                LoaderUtils.hideLoader()

                Toast.makeText(this@LoginActivity, "Login Failed ${t.message}", Toast.LENGTH_SHORT).show()

                Log.d("Login Failed", t.message ?: "Unknown Error")
            }
        }, jsonObject)
    }

    private fun validateData(): Boolean {

        return when {
            binding.editTextPassword.text.isNullOrEmpty() -> {
                binding.editTextPassword.error = "Please enter password"
                false
            }

            binding.editTextPhone.text.isNullOrEmpty() -> {
                binding.editTextPhone.error = "Please enter phone number"
                false
            }

            else -> true
        }
    }
}