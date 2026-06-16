package com.example.wakati_kotlin.dealer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.frontdesk.FrontdeskHomeActivity
import com.example.wakati_kotlin.model.DepositRequest
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityDealerTransferOtpactivityBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DealerTransferOTPActivity : AppCompatActivity() {
    var binding: ActivityDealerTransferOtpactivityBinding? = null
    var targetUserId: String? = null
    var token: String? = null
    var from: String? = null
    var authToken: String? = null
    var sourceUserId: String? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDealerTransferOtpactivityBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        token = sharedPreferences!!.getString("token", "")
        val userId = sharedPreferences!!.getString("user_id", "")
        sourceUserId = sharedPreferences!!.getString("user_id", "")
        StatusBarUtils.setStatusBar(this, true)
        authToken = "Bearer $token"
        val amount = intent.getStringExtra("amount")
        val message = intent.getStringExtra("message")
        val openingBalance = intent.getStringExtra("openingBalance")
        targetUserId = intent.getStringExtra("targetUserId")
        from = intent.getStringExtra("from")
        val userType = intent.getStringExtra("userType")
        // BACK
        binding!!.backArrow.setOnClickListener { v: View? -> finish() }
        // OTP MOVE
        setupOtpInputs()
        // TIMER
        startOtpTimer()

        // PROCEED
        binding!!.btnProceed.setOnClickListener { v: View? ->
            val otp =
                binding!!.otp1.text.toString() + binding!!.otp2.text.toString() + binding!!.otp3.text.toString() + binding!!.otp4.text.toString()
            if (otp.length != 4) {
                Toast.makeText(this, "Enter Valid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            depositMoney(
                amount,
                message,
                openingBalance
            )
        }
    }

    private fun setupOtpInputs() {
        moveNext(binding!!.otp1, binding!!.otp2)
        moveNext(binding!!.otp2, binding!!.otp3)
        moveNext(binding!!.otp3, binding!!.otp4)
    }

    private fun moveNext(current: EditText, next: EditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) {
                    next.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun startOtpTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding!!.txtResend.text = "Resend in " + millisUntilFinished / 1000 + " sec"
            }

            override fun onFinish() {
                binding!!.txtResend.text = "Resend Code"
            }
        }.start()
    }

    private fun depositMoney(amount: String?, message: String?, openingBalance: String?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        val request =
            DepositRequest(targetUserId, sourceUserId, amount!!.toInt(), "CASH_WITHDRAWAL", message)
        Log.d("withdraw_request", request.toString())
        retrofitClass.transactionMoney(object : Callback<LoginResponse> {


            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                LoaderUtils.hideLoader()
                if (response.isSuccessful && response.body() != null) {
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    Log.d("withdraw_responce", json)
                    Toast.makeText(
                        this@DealerTransferOTPActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    var destinationClass: Class<*>? = null
                    when (from) {
                        "DEALER" -> destinationClass = DealerHomeActivity::class.java
                        "FRONT_DESK" -> destinationClass = FrontdeskHomeActivity::class.java
                    }
                    if (destinationClass != null) {
                        val intent = Intent(this@DealerTransferOTPActivity, destinationClass)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@DealerTransferOTPActivity,
                        "Transaction Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@DealerTransferOTPActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        }, authToken!!, request)
    }
}