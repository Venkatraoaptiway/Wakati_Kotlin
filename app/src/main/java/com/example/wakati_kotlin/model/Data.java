package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("mobileNo")
    private String mobileNo;

    public String getMobileNo() {
        return mobileNo;
    }

    public String getUser_id() {
        return user_id;
    }
}
