package com.example.wakati_kotlin.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.wakati_kotlin.R;
import com.example.wakati_kotlin.utils.StatusBarUtils;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        StatusBarUtils.setStatusBar(this, true);

    }
}