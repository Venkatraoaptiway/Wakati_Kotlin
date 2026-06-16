package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName;

public class TransactionData {

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("type")
    private String type;

    @SerializedName("direction")
    private String direction;

    @SerializedName("sender_name")
    private String senderName;

    @SerializedName("sender_mobile")
    private String senderMobile;

    @SerializedName("receiver_name")
    private String receiverName;

    @SerializedName("receiver_mobile")
    private String receiverMobile;

    @SerializedName("transaction_amount")
    private int transactionAmount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("remarks")
    private String remarks;

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public String getDirection() {
        return direction;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getRemarks() {
        return remarks;
    }
}