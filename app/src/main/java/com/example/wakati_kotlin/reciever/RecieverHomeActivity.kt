package com.example.wakati_kotlin.reciever

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
import com.example.wakati_kotlin.model.DashboardResponse
import com.example.wakati_kotlin.model.Document
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.customer.LoginActivity
import com.example.wakati_kotlin.customer.ProfileActivity
import com.example.wakati_kotlin.customer.TransactionsListActivity
import com.example.wakati_kotlin.databinding.ActivityRecieverHomeBinding
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

class RecieverHomeActivity() : AppCompatActivity() {
    
    var binding: ActivityRecieverHomeBinding? = null
    var sharedPreferences: SharedPreferences? = null
    var transactionAdapter: TransactionAdapter? = null

    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecieverHomeBinding.inflate(
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

//        Log.d("USER_ID", userId);
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        serviceProfile(authToken, userId)
        serviceReceiverDashboard(authToken, jsonObject)
        serviceTransactions(authToken, userId)
        binding!!.profileImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@RecieverHomeActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        })
        binding!!.logoutIcon.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // Disable back button
            }
        })



        binding!!.superDealerLayout.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, DealersTotalListActivity::class.java)

            intent.putExtra("flag","Total_Super_Dealers_Flow");
            startActivity(intent)

        }

        binding!!.totalDealersLayout.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, DealersTotalListActivity::class.java)
            intent.putExtra("flag","Total_Dealers_Flow");
            startActivity(intent)

        }

        binding!!.totalPartnerAgentLayout.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, DealersTotalListActivity::class.java)

            intent.putExtra("flag","Total_Partner_Agent_Flow");
            startActivity(intent)

        }
        binding!!.totalPaDealersLayout.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, DealersTotalListActivity::class.java)

            intent.putExtra("flag","Total_Partner_Agent_Dealers_Flow");
            startActivity(intent)

        }

        binding!!.totalFrontdeskLayout.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, DealersTotalListActivity::class.java)

            intent.putExtra("flag","Front_Desk_Flow");
            startActivity(intent)

        }
        binding!!.transactions.setOnClickListener {
            val intent = Intent(this@RecieverHomeActivity, TransactionsListActivity::class.java)
            startActivity(intent)
        }


    }

    fun serviceProfile(authToken: String?, userId: String?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.dashboard(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding!!.swipeRefresh.isRefreshing = false
                LoaderUtils.hideLoader()
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
                    val documents = profileData!!.documents
                    for (document: Document in documents) {
                        if (document.documentUrl != null && (document.documentUrl.endsWith(".jpg") || document.documentUrl.endsWith(
                                ".jpeg"
                            ) || document.documentUrl.endsWith(".png"))
                        ) {
                            fetchProfileImage(document.documentId)
                            break
                        }
                    }
                    Toast.makeText(
                        this@RecieverHomeActivity,
                        loginResponse.profileData!!.userType + "  login successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
                LoaderUtils.hideLoader()
                Toast.makeText(this@RecieverHomeActivity, "API ERROR", Toast.LENGTH_SHORT).show()
            }
        }, (authToken)!!, (userId)!!)
    }

    fun serviceReceiverDashboard(authToken: String?, jsonObject: JsonObject?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.receiverPartnerAgentDashboard(object : Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                LoaderUtils.hideLoader()
                if (response.isSuccessful && response.body() != null) {
                    val dashboard = response.body()

                    // Cash Summary
                    binding!!.cashBalance.text =
                        formatKmf(dashboard!!.cash_summary!!.current_balance.toString())
                    binding!!.cashReceived.text =
                       formatKmf(dashboard.cash_summary!!.payments_collected.toString())
                    binding!!.cashReterned.text =formatKmf(dashboard.cash_summary!!.payments_made.toString())

                    // Super Dealers
                    binding!!.superDealerTotal.text = dashboard.super_dealers!!.total.toString()
                    binding!!.superDealerActive.text = dashboard.super_dealers!!.active.toString()
                    binding!!.superDealerInactive.text =
                        dashboard.super_dealers!!.inactive.toString()

                    // Dealers
                    binding!!.dealerTotal.text = dashboard.dealers!!.total.toString()
                    binding!!.dealerActive.text = dashboard.dealers!!.active.toString()
                    binding!!.dealerInactive.text = dashboard.dealers!!.inactive.toString()

                    // Partner Agent
                    binding!!.partnerAgentTotal.text = dashboard.partner_agents!!.total.toString()
                    binding!!.partnerAgentActive.text = dashboard.partner_agents!!.active.toString()
                    binding!!.partnerAgentInactive.text =
                        dashboard.partner_agents!!.inactive.toString()

                    // P A dealers
                    binding!!.paDealersTotal.text =
                        dashboard.partner_agent_dealers!!.total.toString()
                    binding!!.paDealersActive.text =
                        dashboard.partner_agent_dealers!!.active.toString()
                    binding!!.paDealersInactive.text =
                        dashboard.partner_agent_dealers!!.inactive.toString()

                    // Front Desk
                    binding!!.frontdeskTotal.text = dashboard.front_desk!!.total.toString()
                    binding!!.frontdeskActive.text = dashboard.front_desk!!.active.toString()
                    binding!!.frontdeskInactive.text = dashboard.front_desk!!.inactive.toString()
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                LoaderUtils.hideLoader()
                Log.e("API_ERROR", (t.message)!!)
            }
        }, (authToken)!!, (jsonObject)!!)
    }

    fun serviceTransactions(authToken: String?, userId: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        Log.d("REQUEST_BODY", jsonObject.toString())
        val retrofitClass = getRetrofit()
        retrofitClass.getTransactions(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
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
                            this@RecieverHomeActivity,
                            transactionResponse.data
                        )
                        binding!!.rvTransferHistory.adapter = transactionAdapter
                    } else {
                        Toast.makeText(
                            this@RecieverHomeActivity,
                            "No transactions found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
                Log.e("API_ERROR", (t.message)!!)
                Toast.makeText(this@RecieverHomeActivity, t.message, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@RecieverHomeActivity, e.message, Toast.LENGTH_SHORT).show()
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