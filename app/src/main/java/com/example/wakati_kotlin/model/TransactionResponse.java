package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private int count;

    @SerializedName("data")
    private List<TransactionData> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<TransactionData> getData() {
        return data;
    }
}