package com.example.wakati_kotlin.customer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.api.RetrofitClass
import com.example.wakati_kotlin.agent.AgentHomeActivity
import com.example.wakati_kotlin.dealer.DealerHomeActivity
import com.example.wakati_kotlin.frontdesk.FrontdeskHomeActivity
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.partnerAgent.PartnerAgentHomeActivity
import com.example.wakati_kotlin.reciever.RecieverHomeActivity
import com.example.wakati_kotlin.superDealer.SuperDealerHomeActivity
import com.example.wakati_kotlin.utils.LoaderUtils
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityLoginPinBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPinBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var phone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBar(this, false)

        sharedPreferences = getSharedPreferences("Wakati", MODE_PRIVATE)
        phone = intent.getStringExtra("phone_number") ?: ""
        val fullName = intent.getStringExtra("fullName") ?: ""
        binding.userName.text = fullName
        val userId = intent.getStringExtra("user_id") ?: ""
        binding.forgotPin.setOnClickListener {

            val jsonObject = JsonObject().apply { addProperty("mobile_no", "269$phone") }

            observeForgotPin(jsonObject)
        }


        binding.loginWith.setOnClickListener {
            val intent = Intent(this@LoginPinActivity,LoginActivity::class.java);
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // Disable back button
            }
        })

        binding.proceed.setOnClickListener {
            if (binding.et1.text.toString().isEmpty() || binding.et2.text.toString()
                    .isEmpty() || binding.et3.text.toString()
                    .isEmpty() || binding.et4.text.toString().isEmpty()
            ) {
                Toast.makeText(this, getString(com.example.wakati_kotlin.R.string.enter_pin), Toast.LENGTH_SHORT).show()

            } else {

                val pin =
                    binding.et1.text.toString() + binding.et2.text.toString() + binding.et3.text.toString() + binding.et4.text.toString()

                val jsonObject = JsonObject().apply {
                    addProperty("user_id", userId)
                    addProperty("pin", pin)
                    addProperty("force_logout", true)
                }

                serviceVerifyOTP(jsonObject)
            }
        }

        binding.et1.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                if (binding.et1.text.toString().length == 1) {
                    binding.et2.requestFocus()
                } else {
                    binding.et1.requestFocus()
                }
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })

        binding.et2.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {

                if (binding.et2.text.toString().length == 1) {
                    binding.et3.requestFocus()
                } else {
                    binding.et1.requestFocus()
                }
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })

        binding.et3.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {

                if (binding.et3.text.toString().length == 1) {
                    binding.et4.requestFocus()
                } else {
                    binding.et2.requestFocus()
                }
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })

        binding.et4.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {

                if (binding.et4.text.toString().length == 1) {

                    window.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    )

                    val inputManager = getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as InputMethodManager

                    currentFocus?.let {
                        inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                        )
                    }

                } else {
                    binding.et3.requestFocus()
                }
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })
    }


    private fun serviceVerifyOTP(jsonObject: JsonObject) {

        LoaderUtils.showLoader(this)

        val retrofitClass = RetrofitClass.getRetrofit()

        retrofitClass.loginPin(
            object : Callback<LoginResponse> {

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                    LoaderUtils.hideLoader()

                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        val token = loginResponse.token ?: ""

                        val userId = loginResponse.data?.userId ?: ""
                        val userType = loginResponse.data?.userType ?: ""
                        sharedPreferences.edit().apply {
                            clear()
                            putString("token", token)
                            putString("user_id", userId)
                            apply()
                        }

                        println(response.message())

                        when (userType) {

                            "CUSTOMER" -> {

                                val customer = Intent(
                                    this@LoginPinActivity, CustomerHomeActivity::class.java
                                )
                                println("just for check$userType$userId")
                                startActivity(customer)
                            }

                            "AUTHORISED_DEALER", "INDIVIDUAL_MERCHANT_DEALER", "LEGAL_ENTITY_MERCHANT_DEALER", "DEALER" -> {

                                val dealer = Intent(this@LoginPinActivity, DealerHomeActivity::class.java)
                                println("just for check$userType$userId")
                                startActivity(dealer)
                            }

                            "FRONT_DESK" -> {

                                val frontdesk = Intent(this@LoginPinActivity, FrontdeskHomeActivity::class.java)
                                println("just for check$userType$userId")
                                startActivity(frontdesk)
                            }

                            "RECEIVER" -> {
                                val receiver = Intent(this@LoginPinActivity, RecieverHomeActivity::class.java)
                                println("just for check$userType$userId")
                                startActivity(receiver)
                            }

                            "SUPER_DEALER", "Super Dealer" -> {

                                val superDealer = Intent(this@LoginPinActivity, SuperDealerHomeActivity::class.java
                                )

                                println(
                                    "just for check$userType$userId"
                                )

                                startActivity(superDealer)
                            }

                            "PARTNER_AGENT" -> {

                                val partnerAgent = Intent(
                                    this@LoginPinActivity, PartnerAgentHomeActivity::class.java
                                )

                                println(
                                    "just for check$userType$userId"
                                )

                                startActivity(partnerAgent)
                            }

                            "AGENT" -> {

                                val agent = Intent(
                                    this@LoginPinActivity, AgentHomeActivity::class.java
                                )

                                println(
                                    "just for check$userType$userId"
                                )

                                startActivity(agent)
                            }
                        }

                    } else {

                        Toast.makeText(this@LoginPinActivity, "Invalid Pin", Toast.LENGTH_SHORT).show()

                        android.util.Log.d(
                            "Response", response.message()
                        )
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>, t: Throwable
                ) {

                    LoaderUtils.hideLoader()

                    Toast.makeText(this@LoginPinActivity, "API ERROR", Toast.LENGTH_SHORT).show()
                }

            }, jsonObject
        )
    }

    private fun observeForgotPin(
        jsonObject: JsonObject
    ) {

        LoaderUtils.showLoader(this)

        val retrofitClass = RetrofitClass.getRetrofit()

        retrofitClass.forgotPasswordVerification(
            object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {

                    LoaderUtils.hideLoader()

                    if (response.isSuccessful && response.body() != null) {

                        val loginResponse = response.body()!!

                        val userId = loginResponse.userId ?: ""

                        val otpId = loginResponse.otpId ?: ""

                        val intent = Intent(this@LoginPinActivity, ForgotPasswordOtpActivity::class.java)

                        intent.putExtra("userId", userId)
                        intent.putExtra("otpId", otpId)
                        intent.putExtra("phoneNumber", phone)
                        intent.putExtra("from", "ForgotPin")
                        startActivity(intent)
                    }
                }

                override fun onFailure(

                    call: Call<LoginResponse>, t: Throwable) {

                    LoaderUtils.hideLoader()
                }
            }, jsonObject
        )
    }

}