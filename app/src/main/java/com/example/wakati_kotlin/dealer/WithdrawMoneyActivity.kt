package com.example.wakati_kotlin.dealer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityWithdrawMoneyBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawMoneyActivity : AppCompatActivity() {
    var binding: ActivityWithdrawMoneyBinding? = null
    var sharedPreferences: SharedPreferences? = null
    var from: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawMoneyBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        from = intent.getStringExtra("from")
        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        val token = sharedPreferences!!.getString("token", "")
        val authToken = "Bearer $token"

        // BACK BUTTON
        OnBackPressedDispatcher().addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
            }
        })

        // TOOLBAR BACK
        binding!!.backArrow.setOnClickListener { v: View? -> onBackPressed() }

        // PROCEED
        binding!!.proceed.setOnClickListener { v: View? ->
            val mobileNumber = binding!!.mobile.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(mobileNumber)) {
                Toast.makeText(
                    this@WithdrawMoneyActivity,
                    "Please enter mobile number",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // ADD 269 PREFIX
            val finalMobile = "269$mobileNumber"
            //            String finalMobile = mobileNumber;
            serviceProfile(authToken, finalMobile)
        }

        // QR CLICK
//        binding.scanQr.setOnClickListener(v -> {
//
//            Toast.makeText(this, "Open QR Screen", Toast.LENGTH_SHORT).show();
//
//        });
    }

    fun serviceProfile(authToken: String?, mobileNumber: String?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.profileBymobile(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                LoaderUtils.hideLoader()
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    val gson = Gson()
                    val json = gson.toJson(loginResponse)
                    Log.d("FULL_RESPONSE", json)
                    val profile = loginResponse!!.data
                    if (profile != null) {
                        val userType = profile.userType
                        Log.d("USER_TYPE", userType!!)

                        // VALIDATION
                        if ("CUSTOMER" == userType || "INSTITUTIONAL_USER" == userType) {
                            var idNumber = ""
                            var documentId = ""
                            if (profile.attributes != null) {
                                for ((attributeLabel, attributeValue) in profile.attributes) {
                                    if ("Idnumber".equals(attributeLabel, ignoreCase = true)) {
                                        if (attributeValue != null) {
                                            idNumber = attributeValue
                                        }
                                        Log.d("ID_NUMBER", idNumber)
                                    }
                                }
                            }
                            if (profile.documents != null) {
                                for (document in profile.documents) {
                                    if ("PHOTO".equals(
                                            document.documentType,
                                            ignoreCase = true
                                        ) || "PORTRAIT_PHOTO".equals(
                                            document.documentType,
                                            ignoreCase = true
                                        )
                                    ) {
                                        documentId = document.documentId
                                        Log.d("DOCUMENT_ID", documentId)
                                    }
                                }
                            }
                            val intent = Intent(
                                this@WithdrawMoneyActivity,
                                WithdrawMoneyDetailsActivity::class.java
                            )
                            intent.putExtra("fullName", profile.fullName)
                            intent.putExtra("mobileNo", profile.mobileNo)
                            intent.putExtra("userType", profile.userType)
                            intent.putExtra("user_id", profile.userId)
                            intent.putExtra("idNumber", idNumber)
                            intent.putExtra("documentId", documentId)
                            intent.putExtra("from", from)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@WithdrawMoneyActivity,
                                "Invalid User",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this@WithdrawMoneyActivity, "USER NOT FOUND", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@WithdrawMoneyActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message!!)
            }
        }, authToken!!, mobileNumber!!)
    }
}