package com.example.wakati_kotlin.superDealer

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.adapter.TransactionAdapter
import com.example.wakati_kotlin.model.Document
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.customer.LoginActivity
import com.example.wakati_kotlin.customer.ProfileActivity
import com.example.wakati_kotlin.customer.TransactionsListActivity
import com.example.wakati_kotlin.databinding.ActivitySuperDealerHomeBinding
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
import java.util.Locale

class SuperDealerHomeActivity() : AppCompatActivity() {
    var binding: ActivitySuperDealerHomeBinding? = null
    var currentCard = 1
    var sharedPreferences: SharedPreferences? = null
    var transactionAdapter: TransactionAdapter? = null

    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperDealerHomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, false)
        binding!!.rvTransferHistory.layoutManager = LinearLayoutManager(this)
        binding!!.swipeRefresh.setOnRefreshListener {
            val token: String? = sharedPreferences!!.getString("token", "")
            val userId: String? = sharedPreferences!!.getString("user_id", "")
            val authToken: String = "Bearer " + token

            // REFRESH APIS
            serviceProfile(authToken, userId)
            serviceTransactions(authToken, userId)
        }
        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        val token = sharedPreferences!!.getString("token", "")
        val userId = sharedPreferences!!.getString("user_id", "")
        val authToken = "Bearer $token"
        Log.d("TOKEN", authToken)
        Log.d("USER_ID", (userId)!!)

//        String userId = getIntent().getStringExtra("user_id");
//
//        Log.d("USER_ID", userId);
        serviceProfile(authToken, userId)
        serviceTransactions(authToken, userId)
        binding!!.profileImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SuperDealerHomeActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        })
        binding!!.logoutIcon.setOnClickListener {
            val intent = Intent(this@SuperDealerHomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding!!.viewTransition.setOnClickListener {
            val intent =
                Intent(this@SuperDealerHomeActivity, TransactionsListActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // Disable back button
            }
        })
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
                    val profileData = loginResponse!!.profileData
                    val gson = Gson()
                    val json = gson.toJson(loginResponse)
                    Log.d("FULL_RESPONSE", json)

                    // PROFILE NAME
                    binding!!.userName.text = loginResponse.profileData!!.fullName

                    // WALLET DATA
                    val walletData = loginResponse.walletData

                    // WALLET BALANCE
                    binding!!.walletBalance.text =formatKmf(walletData!!.walletBalance.toString())

                    // AVAILABLE BALANCE
                    binding!!.availableBalance.text = formatKmf(walletData.cashbalance.toString())

                    // First card (Receiver)
                    binding!!.cashCollected.text =
                        walletData.todaySourceCollection.toString() + " KMF"
                    binding!!.cashDistributed.text =
                        walletData.todaySourceDistributions.toString() + " KMF"
                    binding!!.btnNext.setOnClickListener { v: View? ->
                        if (currentCard == 1) {

                            currentCard = 2
                            binding!!.transationsWith.setText("Transactions With Dealers")
                            binding!!.cashCollectedText.setText("Cash Collected")
                            binding!!.cashDistributedText.setText("Cash Distributed")
                            binding!!.cashCollected.setText(formatKmf(walletData.todayDealersCollection.toString()))
                            binding!!.cashDistributed.setText(formatKmf(walletData.todayDealersDistributions.toString()))
                            binding!!.btnNext.setImageResource(R.drawable.baseline_arrow_back_24)

                        } else {
                            currentCard = 1

                            binding!!.transationsWith.setText("Transactions With Receiver")
                            binding!!.cashCollectedText.setText("Cash Received")
                            binding!!.cashDistributedText.setText("Cash Returned")
                            binding!!.cashCollected.setText(formatKmf(walletData.todaySourceCollection.toString()))
                            binding!!.cashDistributed.setText(walletData.todaySourceDistributions.toString() + " KMF")
                            binding!!.btnNext.setImageResource(R.drawable.baseline_arrow_forward_24)
                        }
                    }
                    val documents = profileData!!.documents
                    for (document: Document in documents) {
                        if (document.documentUrl != null && (document.documentUrl.endsWith(".jpg") || document.documentUrl.endsWith(".jpeg"
                            ) || document.documentUrl.endsWith(".png"))
                        ) {
                            fetchProfileImage(document.documentId)
                            break
                        }
                    }
                    Toast.makeText(
                        this@SuperDealerHomeActivity,
                        loginResponse.profileData!!.userType + "  login successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
                Toast.makeText(this@SuperDealerHomeActivity, "API ERROR", Toast.LENGTH_SHORT).show()
            }
        }, (authToken)!!, (userId)!!)
    }

    fun serviceTransactions(authToken: String?, userId: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        Log.d("REQUEST_BODY", jsonObject.toString())
        val retrofitClass = getRetrofit()
        retrofitClass.getTransactions(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse?>,
                response: Response<TransactionResponse?>
            ) {
                binding!!.swipeRefresh.isRefreshing = false
                Log.d("API_CODE", response.code().toString())
                if (response.body() != null) {
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    Log.d("TRANSACTION_RESPONSE", json)
                }
                if (response.isSuccessful && response.body() != null) {
                    val transactionResponse = response.body()
                    if (transactionResponse!!.data != null && !transactionResponse.data.isEmpty()) {
                        transactionAdapter = TransactionAdapter(
                            this@SuperDealerHomeActivity,
                            transactionResponse.data
                        )
                        binding!!.rvTransferHistory.adapter = transactionAdapter
                    } else {
                        Toast.makeText(
                            this@SuperDealerHomeActivity,
                            "No transactions found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<TransactionResponse?>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
                Log.e("API_ERROR", (t.message)!!)
                Toast.makeText(this@SuperDealerHomeActivity, t.message, Toast.LENGTH_LONG).show()
            }
        }, (authToken)!!, jsonObject)
    }

    private fun fetchProfileImage(documentId: String) {
        val token = sharedPreferences!!.getString("token", "")
        val authToken = "Bearer $token"
        val imageUrl = "$baseurl/kyc_document?id=$documentId"
        Log.d("IMAGE_API", imageUrl)
        LoaderUtils.showLoader(this)
        val client = OkHttpClient()
        val request = Request.Builder().url(imageUrl).addHeader("Authorization", authToken)
            .addHeader("Content-Type", "application/json").post(
            RequestBody.create(
                MediaType.parse("application/json"), ""
            )
        ).build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    LoaderUtils.hideLoader()
                    Toast.makeText(this@SuperDealerHomeActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                LoaderUtils.hideLoader()
                if (response.isSuccessful) {
                    val imageBytes = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    runOnUiThread { binding!!.profileImage.setImageBitmap(bitmap) }
                }
            }
        })
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