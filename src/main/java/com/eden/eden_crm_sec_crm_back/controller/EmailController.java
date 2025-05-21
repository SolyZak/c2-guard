package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.AsyncEmailService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/emails")
public class EmailController {
    private final AsyncEmailService emailService;

    @GetMapping("/send-qr-email")
    public ApiResponse<String> sendQREmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam String qrCodeBase64
    ) {
        try {
            emailService.sendEmailWithQrCode(to, subject, body, qrCodeBase64);
        } catch (Exception e) {
            log.error("Error sending qr email to {}", to);
            return ApiResponse.badRequest(MessageUtil.getMessage("email.failed"));
        }
        return ApiResponse.ok(MessageUtil.getMessage("email.sent"));
    }
}
