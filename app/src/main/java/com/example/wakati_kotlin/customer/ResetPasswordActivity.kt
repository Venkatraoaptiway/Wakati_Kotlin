package com.example.wakati_kotlin.customer

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityResetPasswordBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    var binding: ActivityResetPasswordBinding? = null
    var userId: String? = null
    var otpId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        userId = intent.getStringExtra("userId")
        otpId = intent.getStringExtra("otpId")
        Log.d("check the date", "$userId and$otpId")
        setupPasswordToggle(binding!!.password)
        setupPasswordToggle(binding!!.confirmPassword)
        binding!!.btnContinue.setOnClickListener(View.OnClickListener {
            val password = binding!!.password.text.toString().trim { it <= ' ' }
            val confirmPassword = binding!!.confirmPassword.text.toString().trim { it <= ' ' }

            // EMPTY PASSWORD
            if (password.isEmpty()) {
                Toast.makeText(this@ResetPasswordActivity, "Enter Password", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            // PASSWORD LENGTH
            if (password.length < 6) {
                Toast.makeText(this@ResetPasswordActivity, "Password must be minimum 6 characters", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            // EMPTY CONFIRM PASSWORD
            if (confirmPassword.isEmpty()) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Enter Confirm Password",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            // PASSWORD MATCH CHECK
            if (password != confirmPassword) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Password does not match",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", userId)
            jsonObject.addProperty("otp_id", otpId)
            jsonObject.addProperty("password", password)
            observePassword(jsonObject)
        })
    }

    fun observePassword(jsonObject: JsonObject?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.forgotPassword(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                val loginResponse = response.body()
                Toast.makeText(this@ResetPasswordActivity, loginResponse!!.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Api Error " + t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, jsonObject!!)
    }

    private fun setupPasswordToggle(editText: EditText) {
        editText.setOnTouchListener { v: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2
            if (event.rawX >=
                (editText.right
                        - editText.compoundDrawables[DRAWABLE_RIGHT]
                    .bounds.width())
            ) {

                // SHOW PASSWORD
                if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    editText.inputType = InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {

                    // HIDE PASSWORD
                    editText.inputType = InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                editText.setSelection(editText.text.length)
                return@setOnTouchListener true
            }
            false
        }
    }
}