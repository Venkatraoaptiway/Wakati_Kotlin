package com.example.wakati_kotlin.customer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wakati_kotlin.databinding.ActivityCreateAccountOtpBinding;

public class CreateAccountOtpActivity extends AppCompatActivity {


    ActivityCreateAccountOtpBinding binding;


    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("user_id");

//        startTimer();

//        binding.btnverify.setOnClickListener(v -> verifyOtp());

//        binding.txtResend.setOnClickListener(v -> resendOtp());
    }

//    private void verifyOtp() {
//
//        String otp =
//                binding.et1.getText().toString() +
//                        binding.et2.getText().toString() +
//                        binding.et3.getText().toString() +
//                        binding.et4.getText().toString();
//
//        if (otp.length() != 4) {
//
//            Toast.makeText(this,
//                    "Enter Valid OTP",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        LoaderUtils.showLoader(this);
//
//        JsonObject jsonObject = new JsonObject();
//
//        jsonObject.addProperty("user_id", userId);
//        jsonObject.addProperty("otp", otp);
//
//        RetrofitClass.()
//                .verifyOtp(new Callback<LoginResponse>() {
//
//                    @Override
//                    public void onResponse(
//                            Call<LoginResponse> call,
//                            Response<LoginResponse> response) {
//
//                        LoaderUtils.hideLoader();
//
//                        if (response.isSuccessful()
//                                && response.body() != null) {
//
//                            if (response.body().getCode() == 200) {
//
//                                Intent intent =
//                                        new Intent(
//                                                CreateAccountOtpActivity.this,
//                                                CreateAccountDocumentsActivity.class);
//
//                                startActivity(intent);
//
//                                finish();
//                            }
//
//                            Toast.makeText(
//                                    CreateAccountOtpActivity.this,
//                                    response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(
//                            Call<LoginResponse> call,
//                            Throwable t) {
//
//                        LoaderUtils.hideLoader();
//
//                        Toast.makeText(
//                                CreateAccountOtpActivity.this,
//                                t.getMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }, jsonObject);
//    }

//    private void resendOtp() {
//
//        JsonObject jsonObject = new JsonObject();
//
//        jsonObject.addProperty("user_id", userId);
//
//        jsonObject.addProperty("purpose",
//                "REGISTER");
//
//        RetrofitClass.getRetrofit()
//                .withdrawOtp(new Callback<LoginResponse>() {
//
//                    @Override
//                    public void onResponse(
//                            Call<LoginResponse> call,
//                            Response<LoginResponse> response) {
//
//                        if (response.isSuccessful()
//                                && response.body() != null) {
//
//                            Toast.makeText(
//                                    CreateAccountOtpActivity.this,
//                                    response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(
//                            Call<LoginResponse> call,
//                            Throwable t) {
//
//                        Toast.makeText(
//                                CreateAccountOtpActivity.this,
//                                t.getMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }, jsonObject);
//    }

//    private void startTimer() {
//
//        new CountDownTimer(
//                300000,
//                1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                long minutes =
//                        millisUntilFinished / 1000 / 60;
//
//                long seconds =
//                        millisUntilFinished / 1000 % 60;
//
//                txtTimer.setText(
//                        String.format("%02d:%02d",
//                                minutes,
//                                seconds));
//            }
//
//            @Override
//            public void onFinish() {
//
//                txtTimer.setText("00:00");
//            }
//        }.start();
//    }
}