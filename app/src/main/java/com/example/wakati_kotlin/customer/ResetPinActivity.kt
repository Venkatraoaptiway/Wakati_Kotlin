package com.example.wakati_kotlin.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityResetPinBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPinActivity : AppCompatActivity() {
    var binding: ActivityResetPinBinding? = null
    var userId: String? = null
    var otpId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPinBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        userId = intent.getStringExtra("userId")
        otpId = intent.getStringExtra("otpId")
        Log.d("check the date", "$userId and$otpId")
        binding!!.btnProceed.setOnClickListener(View.OnClickListener { // PIN
            val pin = (binding!!.et1.text.toString()
                    + binding!!.et2.text.toString()
                    + binding!!.et3.text.toString()
                    + binding!!.et4.text.toString())

            // CONFIRM PIN
            val confirmPin = (binding!!.cEt1.text.toString()
                    + binding!!.cEt2.text.toString()
                    + binding!!.cEt3.text.toString()
                    + binding!!.cEt4.text.toString())

            // EMPTY PIN
            if (pin.length != 4) {
                Toast.makeText(
                    this@ResetPinActivity,
                    "Enter 4 digit PIN",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            // EMPTY CONFIRM PIN
            if (confirmPin.length != 4) {
                Toast.makeText(
                    this@ResetPinActivity,
                    "Enter Confirm PIN",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            // PIN MATCH CHECK
            if (pin != confirmPin) {
                Toast.makeText(
                    this@ResetPinActivity,
                    "PIN does not match",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", userId)
            jsonObject.addProperty("otp_id", otpId)
            jsonObject.addProperty("Pin", pin)
            serviceVerifyOTP(jsonObject)
        })
    }

    fun serviceVerifyOTP(jsonObject: JsonObject?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.mpinReset(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@ResetPinActivity, response.message(), Toast.LENGTH_SHORT).show()
                Log.d("Reset pin responce ", response.message())
                val intent = Intent(this@ResetPinActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@ResetPinActivity, "Api Error" + t.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }, jsonObject!!)
    }
}