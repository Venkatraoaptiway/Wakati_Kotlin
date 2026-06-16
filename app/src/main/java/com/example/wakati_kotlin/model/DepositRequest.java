package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName;

public class DepositRequest {

    @SerializedName("source_user_id")
    private String sourceUserId;

    @SerializedName("target_user_id")
    private String targetUserId;

    @SerializedName("amount")
    private int amount;

    @SerializedName("txn_type")
    private String txnType;

    @SerializedName("remarks")
    private String remarks;

    public DepositRequest(String sourceUserId,
                          String targetUserId,
                          int amount,
                          String txnType,
                          String remarks) {

        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.amount = amount;
        this.txnType = txnType;
        this.remarks = remarks;
    }
}