package com.SAFE_MAIL.SAFE_MAIL.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private void sendHtmlEmail(String[] recipients, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", String.join(", ", recipients));
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String getLightEmailTemplate(String title, String alertColor, String icon, String mainMessage, String description, boolean showButtons) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        String buttons = "";
        if (showButtons) {
            buttons = "<div style='text-align: center; margin: 25px 0;'>" +
                    "<a href='https://wa.me/918263878470?text=Hello%20SAFE-V%20Team,%20I%20need%20assistance' " +
                    "style='background: #25D366; color: white; padding: 12px 25px; text-decoration: none; " +
                    "border-radius: 6px; font-weight: bold; display: inline-block; margin: 5px;'>" +
                    "📱 Chat on WhatsApp</a>" +
                    "<a href='https://safev.vercel.app' " +
                    "style='background: #007bff; color: white; padding: 12px 25px; text-decoration: none; " +
                    "border-radius: 6px; font-weight: bold; display: inline-block; margin: 5px;'>" +
                    "🌐 Visit Our Website</a>" +
                    "</div>";
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f9f9f9; }" +
                ".container { max-width: 500px; margin: 0 auto; background: white; border-radius: 10px; padding: 25px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; padding: 15px 0; border-bottom: 2px solid " + alertColor + "; margin-bottom: 20px; }" +
                ".alert-icon { font-size: 40px; margin-bottom: 10px; }" +
                ".alert-title { color: " + alertColor + "; font-size: 22px; font-weight: bold; margin: 10px 0; }" +
                ".message-box { background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 15px 0; border-left: 4px solid " + alertColor + "; }" +
                ".main-message { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 8px; }" +
                ".description { color: #666; font-size: 14px; line-height: 1.4; }" +
                ".info-row { display: flex; justify-content: space-between; margin: 15px 0; padding: 0 10px; }" +
                ".info-label { font-weight: bold; color: #555; }" +
                ".info-value { color: #333; }" +
                ".footer { text-align: center; margin-top: 20px; padding-top: 15px; border-top: 1px solid #ddd; color: #888; font-size: 12px; }" +
                ".timestamp { color: #999; font-size: 12px; text-align: center; margin: 10px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "   <div class='header'>" +
                "       <div class='alert-icon'>" + icon + "</div>" +
                "       <div class='alert-title'>" + title + "</div>" +
                "   </div>" +
                "   <div class='message-box'>" +
                "       <div class='main-message'>" + mainMessage + "</div>" +
                "       <div class='description'>" + description + "</div>" +
                "   </div>" +
                "   <div class='info-row'>" +
                "       <span class='info-label'>Vehicle ID:</span>" +
                "       <span class='info-value'>{{vehicleId}}</span>" +
                "   </div>" +
                "   <div class='info-row'>" +
                "       <span class='info-label'>Time:</span>" +
                "       <span class='info-value'>" + timestamp + "</span>" +
                "   </div>" +
                "   " + buttons +
                "   <div class='footer'>" +
                "       SAFE-V Vehicle Safety System | Automated Alert" +
                "   </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    public void sendAlcoholAlert(String[] recipients, Map<String, String> alertData) {
        String template = getLightEmailTemplate(
                "ALCOHOL DETECTED",
                "#e74c3c",
                "🚫",
                "Alcohol detected - Engine disabled",
                "The driver is drunk and engine will not be allowed to start for safety reasons.",
                false
        ).replace("{{vehicleId}}", alertData.get("vehicleId"));

        sendHtmlEmail(recipients, "🚫 ALCOHOL DETECTED - Engine Disabled", template);
    }

    public void sendAccidentAlert(String[] recipients, Map<String, String> alertData) {
        String template = getLightEmailTemplate(
                "ACCIDENT DETECTED",
                "#c0392b",
                "🚨",
                "Accident detected - Emergency mode activated",
                "Use last live location to track driver for immediate safety assistance.",
                false
        ).replace("{{vehicleId}}", alertData.get("vehicleId"));

        sendHtmlEmail(recipients, "🚨 ACCIDENT DETECTED - Emergency Alert", template);
    }

    public void sendFireAlert(String[] recipients, Map<String, String> alertData) {
        String template = getLightEmailTemplate(
                "FIRE DETECTED",
                "#ff6b35",
                "🔥",
                "Fire detected in vehicle",
                "Immediate attention required - Emergency services notified.",
                false
        ).replace("{{vehicleId}}", alertData.get("vehicleId"));

        sendHtmlEmail(recipients, "🔥 FIRE DETECTED - Emergency Alert", template);
    }

    public void sendGasLeakAlert(String[] recipients, Map<String, String> alertData) {
        String template = getLightEmailTemplate(
                "GAS LEAK DETECTED",
                "#f39c12",
                "⚠️",
                "CNG gas leak observed",
                "Critical situation - Immediate evacuation recommended.",
                false
        ).replace("{{vehicleId}}", alertData.get("vehicleId"));

        sendHtmlEmail(recipients, "⚠️ GAS LEAK DETECTED - Critical Alert", template);
    }

    public void sendTemperatureAlert(String[] recipients, Map<String, String> alertData) {
        String template = getLightEmailTemplate(
                "HIGH TEMPERATURE",
                "#e67e22",
                "🌡️",
                "Temperature above 45°C detected",
                "Vehicle interior temperature has reached critical levels.",
                false
        ).replace("{{vehicleId}}", alertData.get("vehicleId"));

        sendHtmlEmail(recipients, "🌡️ HIGH TEMPERATURE WARNING", template);
    }

    public void sendConfirmationAlert(String[] recipients, Map<String, String> alertData) {
        String template = getLightEmailTemplate(
                "ORDER CONFIRMED",
                "#27ae60",
                "✅",
                "Thank you for ordering SAFE-V",
                "Your order has been confirmed. Our team will contact you soon.",
                true
        ).replace("{{vehicleId}}", "yet to be generated");

        sendHtmlEmail(recipients, "✅ ORDER CONFIRMED - SAFE-V Safety System", template);
    }

    public void sendTestEmail(String[] recipients) {
        String template = getLightEmailTemplate(
                "TEST ALERT",
                "#3498db",
                "🧪",
                "Test email successful",
                "Your SAFE-V email system is working correctly.",
                false
        ).replace("{{vehicleId}}", "TEST_VEHICLE");

        sendHtmlEmail(recipients, "🧪 TEST EMAIL - SAFE-V System", template);
    }
}