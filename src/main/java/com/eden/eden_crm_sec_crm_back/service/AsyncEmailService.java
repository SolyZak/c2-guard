package com.eden.eden_crm_sec_crm_back.service;

public interface AsyncEmailService {
    void sendEmailAsync(String to, String subject, String body);
    void sendHtmlEmailAsync(String to, String subject, String body);
    void sendEmailWithQrCode(String to, String subject, String body, String qrCodeBase64);
}
