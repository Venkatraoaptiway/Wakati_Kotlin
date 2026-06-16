package com.example.wakati_kotlin.reciever

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.adapter.TransactionAdapter
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.api.RetrofitClass.Companion.baseurl
import com.example.wakati_kotlin.databinding.ActivityUsersDetailsCardBinding
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.utils.Constants
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class UsersDetailsCardActivity : AppCompatActivity() {


    var binding: ActivityUsersDetailsCardBinding? = null
    private lateinit var sharedPreferences: SharedPreferences

    private var transactionAdapter: TransactionAdapter? = null

    private var flag: String? = null
    private var userId: String? = null
    private var userType: String? = null
    private var fullName: String? = null

    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersDetailsCardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        StatusBarUtils.setStatusBar(this, true)

        binding!!.rvTransferHistory.layoutManager = LinearLayoutManager(this)

        binding!!.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }

        binding!!.date.text = System.currentTimeMillis().toString()

        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)


        flag = intent.getStringExtra("flag")

//        userId = intent.getStringExtra("user_id")

        userType = intent.getStringExtra("user_type")

        fullName = intent.getStringExtra("full_name")

        Log.d("FLAG_3", flag ?: "")
//        Log.d("USER_ID", userId ?: "")
        Log.d("USER_TYPE", userType ?: "")
        Log.d("FULL_NAME", fullName ?: "")

        val token = sharedPreferences.getString("token", "") ?: ""

//        val userId = sharedPreferences.getString("user_id", "") ?: ""

        val userId = intent.getStringExtra("user_id") ?: ""
        val authToken = "Bearer $token"

        Log.d("TOKEN", authToken)
        Log.d("USER_ID", userId)

        loadData(authToken, userId)

//        serviceProfile(authToken, userId)
//
//        serviceTransactions(authToken, userId)
    }

    private fun loadData(
        authToken: String, userId: String
    ) {

        when (flag) {

            "Total_Super_Dealers_Flow" -> {

                binding!!.header.text = "Super Dealer"


                serviceProfile(
                    authToken, userId
                )
//
//                serviceAssignedDealers(
//                    authToken, userId
//                )
            }

            "Total_Dealers_Flow" -> {
                binding!!.header.text = "Dealer"


                serviceProfile(
                    authToken, userId
                )

                serviceTransactions(
                    authToken, userId
                )
            }

            "Total_Partner_Agent_Flow" -> {
                binding!!.header.text = "Partner Agent"


                serviceProfile(
                    authToken, userId
                )

                serviceTransactions(
                    authToken, userId
                )
            }

            "Total_Partner_Agent_Dealers_Flow" -> {

                binding!!.header.text = "P A Dealer"


                serviceProfile(
                    authToken, userId
                )

                serviceTransactions(
                    authToken, userId
                )
            }


            "Total_PA_Super_Dealers_Flow" -> {

                binding!!.header.text = "P A Super Dealer"


                serviceProfile(
                    authToken, userId
                )
//
//                serviceAssignedDealers(
//                    authToken, userId
//                )
                serviceTransactions(
                    authToken, userId
                )

            }

            "Front_Desk_Flow" -> {
                binding!!.header.text = "Front Desk"


                serviceProfile(
                    authToken, userId
                )

                serviceTransactions(
                    authToken, userId
                )
            }
        }
    }


    private fun serviceProfile(authToken: String, userId: String) {

        val retrofitClass = RetrofitClass.getRetrofit()

        retrofitClass.dashboard(
            object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {

                    binding!!.swipeRefresh.isRefreshing = false

                    if (response.isSuccessful && response.body() != null) {

                        val loginResponse = response.body()!!

                        val profileData = loginResponse.profileData

                        val gson = Gson()

                        val json = gson.toJson(loginResponse)

                        Log.d("FULL_RESPONSE", json)

                        binding!!.username.text = profileData?.fullName ?: ""

                        val walletData = loginResponse.walletData

//                        binding.walletBalance.text = "${walletData?.walletBalance ?: 0.0} KMF"

//                        binding!!.currentbalance.text = "${walletData?.cashbalance ?: 0.0} KMF"
//
//                        binding!!.customerDeposits.text = "${walletData?.todayDeposit ?: 0.0} KMF"
//
//                        binding!!.customerWithdrawal.text =
//                            "${walletData?.todayWithdraw ?: 0.0} KMF"


                        binding!!.currentbalance.text = formatKmf(walletData?.cashbalance)

                        binding!!.customerDeposits.text = formatKmf(walletData?.todayDeposit)

                        binding!!.customerWithdrawal.text = formatKmf(walletData?.todayWithdraw)

//                        binding.securityDeposites.text = "${walletData?.securityDeposit ?: 0.0} KMF"
//
//                        binding.earnedCommission.text = "${walletData?.todayCommission ?: 0.0} KMF"

                        val documents = profileData?.documents

                        documents?.forEach { document ->

                            val url = document.documentUrl

                            if (url != null && (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(
                                    ".png"
                                ))
                            ) {

                                fetchProfileImage(document.documentId ?: "")

                                return@forEach
                            }
                        }

                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {

                    binding!!.swipeRefresh.isRefreshing = false
                    Toast.makeText(this@UsersDetailsCardActivity, "API ERROR", Toast.LENGTH_SHORT)
                        .show()
                }
            }, authToken, userId
        )
    }

    private fun serviceTransactions(
        authToken: String, userId: String
    ) {

        LoaderUtils.showLoader(this)

        val jsonObject = JsonObject().apply {
            addProperty("user_id", userId)
        }

        Log.d("REQUEST_BODY", jsonObject.toString())

        val retrofitClass = RetrofitClass.getRetrofit()

        retrofitClass.getTransactions(

            object : Callback<TransactionResponse> {

                override fun onResponse(
                    call: Call<TransactionResponse>, response: Response<TransactionResponse>
                ) {
                    LoaderUtils.hideLoader()

                    binding!!.swipeRefresh.isRefreshing = false

                    Log.d("API_CODE", response.code().toString())

                    response.body()?.let {

                        val gson = Gson()
                        val json = gson.toJson(it)

                        Log.d("TRANSACTION_RESPONSE", json)
                    }

                    if (response.isSuccessful && response.body() != null) {

                        val transactionResponse = response.body()!!

                        val transactionList = transactionResponse.data

                        if (transactionList != null && transactionList.isNotEmpty()) {

                            transactionAdapter = TransactionAdapter(
                                this@UsersDetailsCardActivity, transactionList
                            )
                            binding!!.rvTransferHistory.adapter = transactionAdapter

                        } else {
                            Toast.makeText(
                                this@UsersDetailsCardActivity,
                                "No transactions found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<TransactionResponse>, t: Throwable
                ) {
                    LoaderUtils.hideLoader()

                    binding!!.swipeRefresh.isRefreshing = false
                    Log.e("API_ERROR", t.message ?: "Unknown Error")
                    Toast.makeText(this@UsersDetailsCardActivity, t.message, Toast.LENGTH_LONG)
                        .show()
                }
            }, authToken, jsonObject
        )
    }


    private fun fetchProfileImage(
        documentId: String
    ) {

        val token = sharedPreferences.getString("token", "") ?: ""

        val authToken = "Bearer $token"

        val imageUrl = "$baseurl/kyc_document?id=$documentId"

        Log.d("IMAGE_API", imageUrl)

        LoaderUtils.showLoader(this)

        val client = OkHttpClient()

        val request = Request.Builder().url(imageUrl).addHeader(
            "Authorization", authToken
        ).addHeader(
            "Content-Type", "application/json"
        ).post(
            RequestBody.create(
                MediaType.parse(
                    "application/json"
                ), ""
            )
        ).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {

                runOnUiThread {
                    LoaderUtils.hideLoader()
                    Toast.makeText(this@UsersDetailsCardActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(
                call: okhttp3.Call, response: okhttp3.Response
            ) {
                LoaderUtils.hideLoader()

                if (response.isSuccessful) {

                    val imageBytes = response.body()?.bytes()
                    if (imageBytes != null) {
                        val bitmap = BitmapFactory.decodeByteArray(
                            imageBytes, 0, imageBytes.size
                        )

                        runOnUiThread {
                            binding!!.profileImage.setImageBitmap(
                                bitmap
                            )
                        }
                    }
                }
            }
        })
    }


    private fun onRefresh() {

        val token = sharedPreferences.getString("token", "") ?: ""
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        val authToken = "Bearer $token"

        serviceProfile(authToken, userId)

        serviceTransactions(authToken, userId)
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