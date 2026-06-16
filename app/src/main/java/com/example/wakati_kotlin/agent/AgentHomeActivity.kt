package com.example.wakati_kotlin.agent

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wakati_kotlin.customer.ProfileActivity
import com.example.wakati_kotlin.utils.StatusBarUtils
import com.example.wakati_kotlin.databinding.ActivityAgentHomeBinding
import com.google.gson.JsonObject

class AgentHomeActivity : AppCompatActivity() {

    var binding: ActivityAgentHomeBinding? = null

    val sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAgentHomeBinding.inflate(layoutInflater);
        setContentView(binding!!.root)

        StatusBarUtils.setStatusBar(this,false);


    }

}