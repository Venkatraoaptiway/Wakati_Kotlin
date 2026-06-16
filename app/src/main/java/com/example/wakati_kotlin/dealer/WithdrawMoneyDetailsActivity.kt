package com.example.wakati_kotlin.dealer

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityWithdrawMoneyDetailsBinding
import com.example.wakati_kotlin.utils.Constants
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class WithdrawMoneyDetailsActivity : AppCompatActivity() {
    var binding: ActivityWithdrawMoneyDetailsBinding? = null
    var authToken: String? = null
    var token: String? = null
    var userName: String? = ""
    var phoneNumber: String? = ""
    var idNumber: String? = ""
    var userId: String? = ""
    var userType: String? = ""
    var documentId: String? = ""
    var from: String? = ""

    private lateinit var sharedPreferences: SharedPreferences

    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawMoneyDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        StatusBarUtils.setStatusBar(this, true)
        from = intent.getStringExtra("from")

        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)

        token = sharedPreferences.getString("token", "") ?: ""


        // GET INTENT DATA
        userName = intent.getStringExtra("fullName")
        phoneNumber = intent.getStringExtra("mobileNo")
        userType = intent.getStringExtra("userType")
        idNumber = intent.getStringExtra("idNumber")
        userId = intent.getStringExtra("user_id")
        documentId = intent.getStringExtra("documentId")

        // SET DATA
        binding!!.userName.text = userName
        binding!!.phoneNumber.text = phoneNumber
        binding!!.idNumber.text = idNumber

        // BACK
        binding!!.backArrow.setOnClickListener { v: View? -> finish() }

        // PROCEED
        binding!!.proceed.setOnClickListener { v: View? ->
            val intent = Intent(
                this@WithdrawMoneyDetailsActivity,
                WithdrawMoneyAccountActivity::class.java
            )
            intent.putExtra("fullName", userName)
            intent.putExtra("targetUserId", userId)
            intent.putExtra("userType", userType)
            intent.putExtra("from", from)
            startActivity(intent)
        }

        // LOAD IMAGE
        if (documentId != null && !documentId!!.isEmpty()) {
            fetchProfileImage(documentId!!)
        }
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
                    Toast.makeText(this@WithdrawMoneyDetailsActivity, e.message, Toast.LENGTH_SHORT)
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
}