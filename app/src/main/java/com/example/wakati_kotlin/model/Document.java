package com.example.wakati_kotlin.model;

public class Document {

    private String documentId;
    private String documentType;
    private String documentTypeLabel;
    private String documentNumber;
    private String documentUrl;
    private String verificationStatus;

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentTypeLabel() {
        return documentTypeLabel;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}