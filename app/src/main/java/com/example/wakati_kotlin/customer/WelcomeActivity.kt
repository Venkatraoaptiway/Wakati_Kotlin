package com.example.wakati_kotlin.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    var binding: ActivityWelcomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(this, true)
        binding = ActivityWelcomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.buttonLogin.setOnClickListener { v: View? ->
            val intent = Intent(
                this@WelcomeActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
        }
    }
}