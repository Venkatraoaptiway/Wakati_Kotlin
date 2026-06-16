package com.example.wakati_kotlin.customer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityForgotPasswordOtpBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordOtpActivity : AppCompatActivity() {
    var binding: ActivityForgotPasswordOtpBinding? = null
    var userId: String? = null
    var otpId: String? = null
    var phoneNumber: String? = null
    var fromScreen: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordOtpBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        userId = intent.getStringExtra("userId")
        otpId = intent.getStringExtra("otpId")
        phoneNumber = intent.getStringExtra("phoneNumber")
        fromScreen = intent.getStringExtra("from")
        if ("ForgotPin" == fromScreen) {
            binding!!.create.setText(R.string.forgot_pin)
        } else {
            binding!!.create.setText(R.string.reset_password)
        }
        Log.d("bcsjd", fromScreen!!)
        binding!!.phoneNumber.text = "269$phoneNumber"
        binding!!.btnverify.setOnClickListener {
            val pin =
                binding!!.et1.text.toString() + binding!!.et2.text.toString() + binding!!.et3.text.toString() + binding!!.et4.text.toString()
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", userId)
            jsonObject.addProperty("otp", pin)
            observeOtp(jsonObject)
        }
        binding!!.et1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding!!.et1.text.toString().length == 1) {
                    binding!!.et2.requestFocus()
                } else {
                    binding!!.et1.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.et2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding!!.et2.text.toString().length == 1) {
                    binding!!.et3.requestFocus()
                } else {
                    binding!!.et1.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.et3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding!!.et3.text.toString().length == 1) {
                    binding!!.et4.requestFocus()
                } else {
                    binding!!.et2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.et4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding!!.et4.text.toString().length == 1) {
                    // Hide keyboard
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (currentFocus != null && currentFocus!!.windowToken != null) {
                        inputManager.hideSoftInputFromWindow(
                            currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                        )
                    }
                } else {
                    binding!!.et3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun observeOtp(jsonObject: JsonObject?) {
        LoaderUtils.showLoader(this)
        val retrofitClas = getRetrofit()
        retrofitClas.otpVerification(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                LoaderUtils.hideLoader()
                Log.d("Overall_Responce ", response.message())
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    Toast.makeText(this@ForgotPasswordOtpActivity, "Otp verification successful", Toast.LENGTH_SHORT).show()
                    if ("ForgotPassword" == fromScreen) {
                        val intent = Intent(
                            this@ForgotPasswordOtpActivity,
                            ResetPasswordActivity::class.java
                        )
                        intent.putExtra("userId", userId)
                        intent.putExtra("otpId", otpId)
                        startActivity(intent)
                    } else if ("ForgotPin" == fromScreen) {
                        val intent =
                            Intent(this@ForgotPasswordOtpActivity, ResetPinActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("otpId", otpId)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordOtpActivity, "Invalid Pin", Toast.LENGTH_SHORT).show()
                    Log.d("Responce ", response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@ForgotPasswordOtpActivity, " Api Error " + t.message, Toast.LENGTH_SHORT).show()
            }
        }, jsonObject!!)
    }
}