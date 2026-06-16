package com.example.wakati_kotlin.customer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityProfileBinding
import android.app.Dialog
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.wakati_kotlin.adapter.AttributeAdapter
import com.example.wakati_kotlin.adapter.DocumentAdapter
import com.example.wakati_kotlin.model.Attribute
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.utils.Constants
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var recyclerView: RecyclerView

    val baseurl = Constants.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        StatusBarUtils.setStatusBar(this, true)

        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)

        val token = sharedPreferences.getString("token", "") ?: ""
        val userId = sharedPreferences.getString("user_id", "") ?: ""

        val authToken = "Bearer $token"

        Log.d("TOKEN", authToken)

        Log.d("USER_ID", userId)

        serviceProfile(authToken, userId)
    }


    private fun serviceProfile(authToken: String, userId: String) {

        LoaderUtils.showLoader(this)
        val retrofitClass = RetrofitClass.getRetrofit()
        retrofitClass.dashboard(

            object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {

                    LoaderUtils.hideLoader()

                    if (response.isSuccessful && response.body() != null) {

                        val loginResponse = response.body()!!
                        val profileData = loginResponse.profileData
                        val attributes = profileData?.attributes ?: emptyList()

                        binding.userName.text = profileData?.fullName ?: ""

                        recyclerView = binding.recyclerAttributes

                        recyclerView.layoutManager = LinearLayoutManager(this@ProfileActivity)
                        val adapter = AttributeAdapter(this@ProfileActivity, attributes)

                        recyclerView.adapter = adapter

                        // ============================
                        // DOCUMENTS
                        // ============================

                        val documents = profileData?.documents ?: emptyList()

                        for (document in documents) {

                            if (document.documentUrl != null && (document.documentUrl!!.endsWith(".jpg") || document.documentUrl!!.endsWith(
                                    ".jpeg"
                                ) || document.documentUrl!!.endsWith(".png"))
                            ) {

                                fetchProfileImage(document.documentId ?: "")

                                break
                            }
                        }

                        for (document in documents) {

                            Log.d("DOCUMENT_ID", document.documentId ?: "")
                            Log.d("DOCUMENT_TYPE", document.documentType ?: "")
                            Log.d("DOCUMENT_LABEL", document.documentTypeLabel ?: "")
                            Log.d("DOCUMENT_NUMBER", document.documentNumber ?: "")
                            Log.d("DOCUMENT_URL", document.documentUrl ?: "")
                            Log.d("DOCUMENT_STATUS", document.verificationStatus ?: "")
                        }



                        binding.document.setOnClickListener {

                            val dialog = Dialog(this@ProfileActivity)
                            dialog.setContentView(R.layout.dialog_documents)

                            recyclerView = dialog.findViewById(R.id.recyclerDocuments)
                            recyclerView.layoutManager = LinearLayoutManager(this@ProfileActivity)
                            recyclerView.adapter = DocumentAdapter(this@ProfileActivity, documents)

                            dialog.show()
                        }

                    } else {

                        LoaderUtils.hideLoader()
                        Toast.makeText(this@ProfileActivity, "Response Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {

                    LoaderUtils.hideLoader()

                    Toast.makeText(this@ProfileActivity, t.message, Toast.LENGTH_SHORT).show()

                    Log.d("API_ERROR", t.message ?: "Unknown Error")
                }
            }, authToken, userId
        )
    }


    private fun getAttributeValue(
        attributes: List<Attribute>?, vararg keys: String
    ): String {

        if (attributes == null) {
            return ""
        }

        for (attr in attributes) {

            val label = attr.attributeLabel ?: continue

            for (key in keys) {

                if (label.equals(key, ignoreCase = true)) {
                    return attr.attributeValue ?: ""
                }
            }
        }

        return ""
    }


    private fun fetchProfileImage(
        documentId: String
    ) {

        val token = sharedPreferences.getString(
            "token", ""
        ) ?: ""

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
                    Toast.makeText(this@ProfileActivity, e.message, Toast.LENGTH_SHORT).show()
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

}