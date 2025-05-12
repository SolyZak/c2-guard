package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.service.AsyncEmailService;
import jakarta.activation.FileDataSource;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncEmailServiceImpl implements AsyncEmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Async
    public void sendEmailAsync(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(fromEmail);

        try {
            mailSender.send(message);
            log.info("Send Mail Success To: {}", to);
        } catch (Exception e) {
            log.error("failed to send mail to {} with error {}", to, e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmailAsync(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            mimeMessage.setContent(body, "text/html;charset=utf-8");

            messageHelper.setFrom(fromEmail);
            messageHelper.setSubject(subject);
            messageHelper.setTo(to);

            mailSender.send(mimeMessage);

            log.info("Send HTML Mail Success To: {}", to);
        } catch (Exception e) {
            log.error("failed to send html mail to {} with error {}", to, e.getMessage());
        }
    }

    @Async
    public void sendEmailWithQrCode(String to, String subject, String body, String qrCodeBase64) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // Use multipart message for inline images
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");

            messageHelper.setFrom(fromEmail);
            messageHelper.setSubject(subject);
            messageHelper.setTo(to);

            // Decode the Base64 QR code string to bytes
            byte[] qrCodeBytes = Base64.getDecoder().decode(qrCodeBase64);

            // Create a unique temporary file for the QR code image
            String uniqueFileName = "qrCode_" + UUID.randomUUID() + ".png";
            File tempFile = new File(System.getProperty("java.io.tmpdir"), uniqueFileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(qrCodeBytes);
            }
            messageHelper.addAttachment("Visit QR Code", new FileDataSource(tempFile));
            messageHelper.setText(body, true); // true to enable HTML content
            mailSender.send(message);
            log.info("Success sending qr email to {}", to);
        } catch (Exception e)
        {
            log.error("Error sending qr email to {}, exception: {}", to, e.getMessage());
        }
    }
}
