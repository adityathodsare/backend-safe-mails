package com.SAFE_MAIL.SAFE_MAIL.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.iot.recipients:adityaamolthodsare@gmail.com}")
    private String iotRecipients;

    @Value("${app.email.sender:thodsareaditya@gmail.com}")
    private String senderEmail;

    /**
     * Send HTML email to a single recipient
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML content

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Send HTML email to multiple recipients
     */
    public void sendHtmlEmailToMultiple(List<String> recipients, String subject, String htmlBody) {
        int successCount = 0;
        int failureCount = 0;

        for (String recipient : recipients) {
            try {
                sendHtmlEmail(recipient.trim(), subject, htmlBody);
                successCount++;
            } catch (Exception e) {
                System.err.println("❌ Failed to send email to: " + recipient);
                failureCount++;
            }
        }

        System.out.println("📧 Email sending summary: " + successCount + " successful, " + failureCount + " failed");
    }

    /**
     * Send plain text email
     */
    public void sendTextEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // false = plain text

            mailSender.send(message);
            System.out.println("✅ Text email sent to: " + to);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send text email: " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Special method for IoT alerts - sends to predefined recipients
     */
    public void sendIotAlertEmail(String subject, String htmlBody) {
        try {
            List<String> recipients = Arrays.asList(iotRecipients.split(","));
            sendHtmlEmailToMultiple(recipients, subject, htmlBody);
        } catch (Exception e) {
            System.err.println("❌ Failed to send IoT alert email: " + e.getMessage());
            throw new RuntimeException("IoT alert email sending failed", e);
        }
    }

    /**
     * Send emergency alert with enhanced formatting
     */
    public void sendEmergencyAlert(String alertType, String deviceId, String additionalInfo) {
        String subject = "🚨 EMERGENCY: " + alertType.toUpperCase() + " - Device: " + deviceId;

        String htmlBody = buildEmergencyEmailTemplate(alertType, deviceId, additionalInfo);
        sendIotAlertEmail(subject, htmlBody);
    }

    private String buildEmergencyEmailTemplate(String alertType, String deviceId, String additionalInfo) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background: #f9f9f9; padding: 20px; }" +
                ".container { max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 4px 10px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; color: #ff0000; border-bottom: 2px solid #ff0000; padding-bottom: 10px; }" +
                ".alert-details { background: #fff3f3; padding: 15px; border-radius: 5px; margin: 15px 0; }" +
                ".footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🚨 IOT EMERGENCY ALERT</h1>" +
                "</div>" +
                "<div class='alert-details'>" +
                "<h2>Alert Type: " + alertType.toUpperCase() + "</h2>" +
                "<p><strong>Device ID:</strong> " + deviceId + "</p>" +
                "<p><strong>Time:</strong> " + java.time.LocalDateTime.now() + "</p>" +
                "<p><strong>Additional Info:</strong> " + additionalInfo + "</p>" +
                "</div>" +
                "<p>This is an automated emergency alert from your IoT system. Please take immediate action.</p>" +
                "<div class='footer'>" +
                "<p>This is an automated email, please do not reply.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}