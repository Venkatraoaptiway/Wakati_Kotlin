package com.example.wakati_kotlin.customer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.databinding.ActivityCustomerHomeBinding
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.StatusBarUtils.setStatusBar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CustomerHomeActivity : AppCompatActivity() {
    var binding: ActivityCustomerHomeBinding? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerHomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        setStatusBar(this, false)
        binding!!.swipeRefresh.setOnRefreshListener {
            val token = sharedPreferences!!.getString("token", "")
            val userId = sharedPreferences!!.getString("user_id", "")
            val authToken = "Bearer $token"

            // REFRESH APIS
            serviceProfile(authToken, userId)
        }
        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        val token = sharedPreferences!!.getString("token", "")
        val userId = sharedPreferences!!.getString("user_id", "")
        binding!!.myAccount.setOnClickListener {
            val intent = Intent(this@CustomerHomeActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        binding!!.logoutIcon.setOnClickListener {
            val intent = Intent(this@CustomerHomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        val authToken = "Bearer $token"
        Log.d("Cistoemr_TOKEN", authToken)
        Log.d("Customer_USER_ID", userId!!)
        serviceProfile(authToken, userId)
    }

    fun serviceProfile(authToken: String?, userId: String?) {
        val retrofitClass = getRetrofit()
        retrofitClass.dashboard(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                binding!!.swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    val gson = Gson()
                    val json = gson.toJson(loginResponse)
                    Log.d("FULL_RESPONSE", json)

                    // PROFILE NAME
                    binding!!.userName.text = loginResponse!!.profileData!!.fullName

                    // WALLET DATA
                    val walletData = loginResponse.walletData

                    // WALLET BALANCE
                    binding!!.availableBalance.text = formatKmf(walletData!!.walletBalance.toString())
                    Toast.makeText(
                        this@CustomerHomeActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
            }
        }, authToken!!, userId!!)
    }

    private fun formatKmf(value: Any?): String {

        val amount = when (value) {
            is String -> value.toDoubleOrNull() ?: 0.0
            is Number -> value.toDouble()
            else -> 0.0
        }

        return String.format(
            Locale.US, "%,d", amount.toLong()
        ).replace(",", " ") + " KMF"
    }

}