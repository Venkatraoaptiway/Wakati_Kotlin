package com.example.wakati_kotlin.frontdesk

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.adapter.TransactionAdapter
import com.example.wakati_kotlin.customer.LoginActivity
import com.example.wakati_kotlin.customer.ProfileActivity
import com.example.wakati_kotlin.customer.TransactionsListActivity
import com.example.wakati_kotlin.dealer.DepositeMoneyActivity
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityFrontdeskHomeBinding
import com.example.wakati_kotlin.dealer.WithdrawMoneyActivity
import com.example.wakati_kotlin.model.Document
import com.example.wakati_kotlin.utils.Constants
import com.example.wakati_kotlin.utils.LoaderUtils
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

class FrontdeskHomeActivity() : AppCompatActivity() {
    var binding: ActivityFrontdeskHomeBinding? = null
    private lateinit var sharedPreferences: SharedPreferences
    var transactionAdapter: TransactionAdapter? = null

    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrontdeskHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.rvTransferHistory.layoutManager = LinearLayoutManager(this)
        StatusBarUtils.setStatusBar(this, false)
        binding!!.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }




        binding!!.myAccount.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@FrontdeskHomeActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        })
        binding!!.profileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(this@FrontdeskHomeActivity, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        })
        binding!!.logoutIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(this@FrontdeskHomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        })
        binding!!.transactions.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(this@FrontdeskHomeActivity, TransactionsListActivity::class.java)
                startActivity(intent)
            }
        })
        binding!!.depositMoney.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(this@FrontdeskHomeActivity, DepositeMoneyActivity::class.java)
//                navigation.navigate('Depositemoney', { from: 'DealerHome', flag: "Dealer" });
                intent.putExtra("from", "FRONT_DESK")
                startActivity(intent)
            }
        })
        binding!!.withdrawalMoney.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(this@FrontdeskHomeActivity, WithdrawMoneyActivity::class.java)
//                navigation.navigate('Depositemoney', { from: 'DealerHome', flag: "Dealer" });
                intent.putExtra("from", "FRONT_DESK")
                startActivity(intent)
            }
        })
        binding!!.etSearch.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (transactionAdapter != null) {
                        transactionAdapter!!
                            .filter
                            .filter(s)
                    }
                }

                override fun afterTextChanged(
                    s: Editable
                ) {
                }
            })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // Disable back button
            }
        })


        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)

        val token = sharedPreferences.getString("token", "") ?: ""

        val userId = sharedPreferences.getString("user_id", "") ?: ""

        val authToken = "Bearer $token"
        Log.d("TOKEN", authToken)
        Log.d("USER_ID", (userId)!!)

//        String userId = getIntent().getStringExtra("user_id");
//
//        Log.d("USER_ID", userId);
        serviceProfile(authToken, userId)
        serviceTransactions(authToken, userId)
    }

    fun serviceProfile(authToken: String?, userId: String?) {

        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.dashboard(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {

                LoaderUtils.hideLoader()
                binding!!.swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    val profileData = loginResponse!!.profileData

                    val gson = Gson()
                    val json = gson.toJson(loginResponse)
                    Log.d("FULL_RESPONSE", json)

                    // PROFILE NAME
                    binding!!.userName.text = loginResponse!!.profileData!!.fullName

                    // WALLET DATA
                    val walletData = loginResponse.walletData

                    // WALLET BALANCE

                    // AVAILABLE BALANCE
                    binding!!.availableBalance.text = formatKmf(walletData!!.cashbalance.toString())

                    // DEPOSIT
                    binding!!.deposits.text = formatKmf(walletData.todayDeposit.toString())

                    // WITHDRAW
                    binding!!.withdrawals.text = formatKmf(walletData.todayWithdraw.toString())

                    // SECURITY DEPOSIT

                    // COMMISSION
                    binding!!.earnedCommission.text = formatKmf(walletData.todayCommission.toString())

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
                        this@FrontdeskHomeActivity,
                        loginResponse.profileData!!.userType + "  login successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
                LoaderUtils.hideLoader()
                Toast.makeText(this@FrontdeskHomeActivity, "API ERROR", Toast.LENGTH_SHORT).show()
            }
        }, (authToken)!!, (userId)!!)
    }

    fun serviceTransactions(authToken: String?, userId: String?) {
        LoaderUtils.showLoader(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userId)
        Log.d("REQUEST_BODY", jsonObject.toString())
        val retrofitClass = getRetrofit()
        retrofitClass.getTransactions(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse?>,
                response: Response<TransactionResponse?>
            ) {
                LoaderUtils.hideLoader()
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
                            this@FrontdeskHomeActivity,
                            transactionResponse.data
                        )
                        binding!!.rvTransferHistory.adapter = transactionAdapter
                    } else {
                        Toast.makeText(
                            this@FrontdeskHomeActivity,
                            "No transactions found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<TransactionResponse?>, t: Throwable) {
                binding!!.swipeRefresh.isRefreshing = false
                LoaderUtils.hideLoader()
                Log.e("API_ERROR", (t.message)!!)
                Toast.makeText(this@FrontdeskHomeActivity, t.message, Toast.LENGTH_LONG).show()
            }
        }, (authToken)!!, jsonObject)
    }

    private fun onRefresh() {

        val token = sharedPreferences.getString("token", "") ?: ""
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        val authToken = "Bearer $token"

        serviceProfile(authToken, userId)

        serviceTransactions(authToken, userId)
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
                runOnUiThread{
                    LoaderUtils.hideLoader()
                    Toast.makeText(this@FrontdeskHomeActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                LoaderUtils.hideLoader()
                if (response.isSuccessful) {
                    val imageBytes = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    runOnUiThread{ binding!!.profileImage.setImageBitmap(bitmap) }
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