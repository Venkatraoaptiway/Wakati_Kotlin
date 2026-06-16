package com.example.wakati_kotlin.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass.Companion.getRetrofit
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityForgotPasswordBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    var binding: ActivityForgotPasswordBinding? = null

    var userId: String? = null
    var otpId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater);
        setContentView(binding!!.root)

        StatusBarUtils.setStatusBar(this, true)

        binding!!.btnContinue.setOnClickListener {

            var jsonObject = JsonObject()
            jsonObject.addProperty("mobile_no", "269" + binding!!.editTextPhone.text.toString());
            jsonObject.addProperty("purpose", "PASSWORD_RESET")
            observeLogin(jsonObject)
        }


    }


    fun observeLogin(jsonObject: JsonObject?) {
        LoaderUtils.showLoader(this)
        val retrofitClass = getRetrofit()
        retrofitClass.forgotPasswordVerification(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>, response: Response<LoginResponse?>
            ) {
                LoaderUtils.hideLoader()
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    userId = loginResponse!!.userId
                    otpId = loginResponse.otpId
                    val intent =
                        Intent(this@ForgotPasswordActivity, ForgotPasswordOtpActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("otpId", otpId)
                    intent.putExtra("phoneNumber", "269" + binding!!.editTextPhone.text.toString())
                    intent.putExtra("from", "ForgotPassword")
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, response.message(), Toast.LENGTH_SHORT).show()
                    Log.d("Responce ", response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                LoaderUtils.hideLoader()
                Toast.makeText(this@ForgotPasswordActivity, "Api Error" + t.message, Toast.LENGTH_SHORT).show()
            }
        }, jsonObject!!)
    }
}