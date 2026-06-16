package com.example.wakati_kotlin.dealer

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.adapter.TransactionAdapter
import com.example.wakati_kotlin.customer.LoginActivity
import com.example.wakati_kotlin.customer.ProfileActivity
import com.example.wakati_kotlin.customer.TransactionsListActivity
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityDealerHomeBinding
import com.example.wakati_kotlin.utils.Constants
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
import java.lang.reflect.Constructor
import java.util.Locale

class DealerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDealerHomeBinding

    private lateinit var sharedPreferences: SharedPreferences

    private var transactionAdapter: TransactionAdapter? = null


    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDealerHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.rvTransferHistory.layoutManager = LinearLayoutManager(this)

        StatusBarUtils.setStatusBar(this, false)

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }

        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)

        val token = sharedPreferences.getString("token", "") ?: ""

        val userId = sharedPreferences.getString("user_id", "") ?: ""

        binding.myAccount.setOnClickListener {

            val intent = Intent(this@DealerHomeActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.profileImage.setOnClickListener {

            val intent = Intent(this@DealerHomeActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.logoutIcon.setOnClickListener {
            val intent = Intent(this@DealerHomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.transactions.setOnClickListener {
            val intent = Intent(this@DealerHomeActivity, TransactionsListActivity::class.java)
            startActivity(intent)
        }

        binding.depositMoney.setOnClickListener {
            val intent = Intent(this@DealerHomeActivity, DepositeMoneyActivity::class.java)
            intent.putExtra("from", "DEALER")
            startActivity(intent)
        }

        binding.withdrawalMoney.setOnClickListener {
            val intent = Intent(this@DealerHomeActivity, WithdrawMoneyActivity::class.java)
            intent.putExtra("from", "DEALER")
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                transactionAdapter?.filter?.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // Disable back button
            }
        })

        val authToken = "Bearer $token"

        Log.d("TOKEN", authToken)
        Log.d("USER_ID", userId)

        serviceProfile(authToken, userId)

        serviceTransactions(authToken, userId)
    }


    private fun serviceProfile(authToken: String, userId: String) {

        val retrofitClass = RetrofitClass.getRetrofit()

        retrofitClass.dashboard(
            object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    binding.swipeRefresh.isRefreshing = false

                    if (response.isSuccessful && response.body() != null) {

                        val loginResponse = response.body()!!

                        val profileData = loginResponse.profileData

                        val gson = Gson()

                        val json = gson.toJson(loginResponse)

                        Log.d("FULL_RESPONSE", json)

                        binding.userName.text = profileData?.fullName ?: ""

                        val walletData = loginResponse.walletData

                        binding.walletBalance.text = formatKmf(walletData?.walletBalance )

                        binding.availableBalance.text = formatKmf(walletData?.cashbalance )

                        binding.deposits.text = formatKmf(walletData?.todayDeposit )

                        binding.withdrawals.text = formatKmf(walletData?.todayWithdraw )

                        binding.securityDeposites.text = formatKmf(walletData?.securityDeposit )

                        binding.earnedCommission.text =formatKmf(walletData?.todayCommission )




                        val documents = profileData?.documents

                        documents?.forEach { document ->

                            val url = document.documentUrl

                            if (url != null && (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png"))
                            ) {

                                fetchProfileImage(document.documentId ?: "")

                                return@forEach
                            }
                        }
                        Toast.makeText(
                            this@DealerHomeActivity,
                            "${profileData?.userType} login successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {

                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this@DealerHomeActivity, "API ERROR", Toast.LENGTH_SHORT).show()
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

                    binding.swipeRefresh.isRefreshing = false

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
                                this@DealerHomeActivity, transactionList
                            )
                            binding.rvTransferHistory.adapter = transactionAdapter

                        } else {
                            Toast.makeText(
                                this@DealerHomeActivity,
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

                    binding.swipeRefresh.isRefreshing = false
                    Log.e("API_ERROR", t.message ?: "Unknown Error")
                    Toast.makeText(this@DealerHomeActivity, t.message, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@DealerHomeActivity, e.message, Toast.LENGTH_SHORT).show()
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
                            binding.profileImage.setImageBitmap(
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

