package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName;

public class StatementRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("from_date")
    private String fromDate;

    @SerializedName("to_date")
    private String toDate;

    public StatementRequest(String userId,
                            String fromDate,
                            String toDate) {

        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}