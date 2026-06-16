package com.example.wakati_kotlin.dealer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.frontdesk.FrontdeskHomeActivity
import com.example.wakati_kotlin.model.DepositRequest
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityDepositeMoneyAccountBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//import com.example.wakati_kotlin.Model.CommonResponse;
class DepositeMoneyAccountActivity : AppCompatActivity() {
    var binding: ActivityDepositeMoneyAccountBinding? = null
    var sharedPreferences: SharedPreferences? = null
    var authToken = ""
    var sourceUserId: String? = ""
    var targetUserId: String? = ""
    var userType: String? = ""
    var from: String? = ""
    var userName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositeMoneyAccountBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        from = intent.getStringExtra("from")
        userName = intent.getStringExtra("fullName")
        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        val token = sharedPreferences!!.getString("token", "")
        authToken = "Bearer $token"
        sourceUserId = sharedPreferences!!.getString("user_id", "")

        // FROM PREVIOUS SCREEN
        targetUserId = intent.getStringExtra("targetUserId")
        Log.d("TARGET_USER_ID", targetUserId!!)
        userType = intent.getStringExtra("userType")

        // BACK
        binding!!.backArrow.setOnClickListener { v: View? -> onBackPressed() }
        if ("DEALER" == userType) {
            binding!!.openingBalanceLayout.visibility = View.VISIBLE
            binding!!.amountLabel.text = "Security Deposite"
        }

        // PROCEED
        binding!!.proceed.setOnClickListener { v: View? -> validateAndProceed() }
    }

    private fun validateAndProceed() {
        val amount = binding!!.amount.text.toString().trim { it <= ' ' }
        val message = binding!!.message.text.toString().trim { it <= ' ' }
        val openingBalance = binding!!.openingBalance.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show()
            return
        }

        // DEALER CHECK
        if ("DEALER" == userType && TextUtils.isEmpty(openingBalance)) {
            Toast.makeText(this, "Enter Opening Balance", Toast.LENGTH_SHORT).show()
            return
        }
        showConfirmDialog(amount, message, openingBalance)
    }

    //    private void showConfirmDialog(String amount, String message, String openingBalance) {
    //
    //        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //
    //        builder.setTitle("Confirm");
    //
    //        builder.setMessage("Proceed with deposit?\n\n" + "Amount : " + amount);
    //
    //        builder.setPositiveButton("Proceed", (dialog, which) -> {
    //
    //            depositMoney(amount, message, openingBalance);
    //        });
    //
    //        builder.setNegativeButton("Cancel", null);
    //
    //        builder.show();
    //    }
    private fun showConfirmDialog(
        amount: String,
        message: String,
        openingBalance: String
    ) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater
            .inflate(R.layout.dialog_select_account, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(
            android.R.color.transparent
        )
        dialog.setCancelable(true)

        // IDS
        val txtTotal = view.findViewById<TextView>(R.id.txtTotal)
        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        val btnProceed = view.findViewById<TextView>(R.id.btnProceed)
        val holderName = view.findViewById<TextView>(R.id.holdername)


        // SET TOTAL
        txtTotal.text = "$amount KMF"
        holderName.text = userName

        // CLOSE
        btnClose.setOnClickListener { v: View? -> dialog.dismiss() }

        // PROCEED
        btnProceed.setOnClickListener { v: View? ->
            dialog.dismiss()
            depositMoney(
                amount,
                message,
                openingBalance
            )
        }
        dialog.show()
    }

    private fun depositMoney(amount: String, message: String, openingBalance: String) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        val request =
            DepositRequest(sourceUserId, targetUserId, amount.toInt(), "CASH_DEPOSIT", message)
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
                    Log.d("deposite_responce", json)
                    Toast.makeText(
                        this@DepositeMoneyAccountActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    var destinationClass: Class<*>? = null
                    when (from) {
                        "DEALER" -> destinationClass = DealerHomeActivity::class.java
                        "FRONT_DESK" -> destinationClass = FrontdeskHomeActivity::class.java
                    }
                    if (destinationClass != null) {
                        val intent = Intent(
                            this@DepositeMoneyAccountActivity,
                            destinationClass
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@DepositeMoneyAccountActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@DepositeMoneyAccountActivity, t.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }, authToken, request)
    }
}