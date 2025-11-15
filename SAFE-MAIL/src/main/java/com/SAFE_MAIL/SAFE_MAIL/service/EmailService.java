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

    private String getBaseEmailTemplate(String title, String alertColor, String iconEmoji, String content) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 30px auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, " + alertColor + " 0%, " + darkenColor(alertColor) + " 100%); color: white; padding: 30px; text-align: center; }" +
                ".header-icon { font-size: 60px; margin-bottom: 10px; }" +
                ".header-title { font-size: 28px; font-weight: bold; margin: 0; text-shadow: 2px 2px 4px rgba(0,0,0,0.2); }" +
                ".content { padding: 30px; }" +
                ".alert-box { background-color: " + lightenColor(alertColor) + "; border-left: 5px solid " + alertColor + "; padding: 20px; margin: 20px 0; border-radius: 5px; }" +
                ".sensor-data { background-color: #f9f9f9; border-radius: 8px; padding: 20px; margin: 20px 0; }" +
                ".sensor-row { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #e0e0e0; }" +
                ".sensor-row:last-child { border-bottom: none; }" +
                ".sensor-label { font-weight: bold; color: #555; }" +
                ".sensor-value { color: #333; font-weight: 500; }" +
                ".footer { background-color: #2c3e50; color: white; padding: 20px; text-align: center; font-size: 12px; }" +
                ".timestamp { color: #7f8c8d; font-size: 14px; margin-top: 10px; }" +
                ".warning-text { color: " + alertColor + "; font-weight: bold; font-size: 18px; margin: 15px 0; }" +
                ".action-required { background-color: #fff3cd; border: 2px solid #ffc107; padding: 15px; border-radius: 5px; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<div class='header-icon'>" + iconEmoji + "</div>" +
                "<h1 class='header-title'>" + title + "</h1>" +
                "</div>" +
                "<div class='content'>" +
                content +
                "<div class='timestamp'>⏰ Alert Time: " + timestamp + "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<strong>Vehicle Safety Monitor System</strong><br>" +
                "Powered by ESP32 & ThingSpeak | Channel ID: 2897293<br>" +
                "This is an automated alert. Please take immediate action if required." +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String darkenColor(String color) {
        // Simple darkening - in production, use proper color manipulation
        return color.replace("#ff", "#cc").replace("#e74c3c", "#c0392b");
    }

    private String lightenColor(String color) {
        // Simple lightening - in production, use proper color manipulation
        return color + "20"; // Add transparency
    }

    public void sendFireAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>🔥 FIRE DETECTED IN VEHICLE!</p>" +
                "<p>A fire has been detected in the vehicle. Immediate action is required!</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #e74c3c;'>Sensor Readings</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>🌡️ Temperature:</span><span class='sensor-value'>" + sensorData.get("temperature") + "°C</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Location:</span><span class='sensor-value'>" + sensorData.get("location") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>⚠️ IMMEDIATE ACTIONS REQUIRED:</strong><br>" +
                "1. Stop the vehicle immediately<br>" +
                "2. Evacuate all passengers<br>" +
                "3. Call emergency services (Fire Department)<br>" +
                "4. Use fire extinguisher if safe to do so" +
                "</div>";

        sendHtmlEmail(recipients, "🔥 CRITICAL: Fire Alert - Vehicle Safety Monitor",
                getBaseEmailTemplate("FIRE ALERT", "#e74c3c", "🔥", content));
    }

    public void sendGasLeakAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>☣️ GAS LEAK DETECTED!</p>" +
                "<p>Dangerous gas levels have been detected in the vehicle.</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #f39c12;'>Sensor Readings</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>📊 Gas Level:</span><span class='sensor-value'>" + sensorData.get("gasLevel") + " PPM</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🧪 Gas Type:</span><span class='sensor-value'>" + sensorData.get("gasType") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Location:</span><span class='sensor-value'>" + sensorData.get("location") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>⚠️ IMMEDIATE ACTIONS REQUIRED:</strong><br>" +
                "1. Open all windows immediately<br>" +
                "2. Turn off the engine<br>" +
                "3. Evacuate the vehicle<br>" +
                "4. Do not use any electrical switches<br>" +
                "5. Call emergency services" +
                "</div>";

        sendHtmlEmail(recipients, "☣️ CRITICAL: Gas Leak Alert - Vehicle Safety Monitor",
                getBaseEmailTemplate("GAS LEAK ALERT", "#f39c12", "☣️", content));
    }

    public void sendAlcoholAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>🍺 ALCOHOL DETECTED!</p>" +
                "<p>Alcohol has been detected in the driver's breath. The driver should not operate the vehicle.</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #e67e22;'>Sensor Readings</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>🧪 Alcohol Level:</span><span class='sensor-value'>" + sensorData.get("alcoholLevel") + " mg/L</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>👤 Driver:</span><span class='sensor-value'>" + sensorData.get("driverName") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Location:</span><span class='sensor-value'>" + sensorData.get("location") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>⚠️ IMMEDIATE ACTIONS REQUIRED:</strong><br>" +
                "1. Do not allow the driver to operate the vehicle<br>" +
                "2. Find an alternate driver<br>" +
                "3. Contact supervisor/management<br>" +
                "4. Document the incident" +
                "</div>";

        sendHtmlEmail(recipients, "🍺 WARNING: Alcohol Detection Alert - Vehicle Safety Monitor",
                getBaseEmailTemplate("ALCOHOL ALERT", "#e67e22", "🍺", content));
    }

    public void sendTiltAccidentAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>⚠️ VEHICLE TILT/ACCIDENT DETECTED!</p>" +
                "<p>Abnormal vehicle tilt detected. Possible accident or rollover situation.</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #c0392b;'>Sensor Readings</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>📐 Tilt Angle:</span><span class='sensor-value'>" + sensorData.get("tiltAngle") + "°</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>⚡ Speed:</span><span class='sensor-value'>" + sensorData.get("speed") + " km/h</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Location:</span><span class='sensor-value'>" + sensorData.get("location") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>🚨 EMERGENCY ACTIONS REQUIRED:</strong><br>" +
                "1. Call emergency services immediately (Ambulance & Police)<br>" +
                "2. Dispatch emergency response team to location<br>" +
                "3. Attempt to contact driver/passengers<br>" +
                "4. Activate emergency protocols" +
                "</div>";

        sendHtmlEmail(recipients, "🚨 CRITICAL: Tilt/Accident Alert - Vehicle Safety Monitor",
                getBaseEmailTemplate("TILT/ACCIDENT ALERT", "#c0392b", "⚠️", content));
    }

    public void sendPassengerOverloadAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>👥 PASSENGER OVERLOAD DETECTED!</p>" +
                "<p>The vehicle has exceeded its maximum passenger capacity.</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #d35400;'>Sensor Readings</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>👥 Current Passengers:</span><span class='sensor-value'>" + sensorData.get("passengerCount") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>✅ Max Capacity:</span><span class='sensor-value'>" + sensorData.get("maxCapacity") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Location:</span><span class='sensor-value'>" + sensorData.get("location") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>⚠️ IMMEDIATE ACTIONS REQUIRED:</strong><br>" +
                "1. Stop the vehicle immediately<br>" +
                "2. Reduce passenger count to safe levels<br>" +
                "3. Document the violation<br>" +
                "4. Issue warning to driver/operator" +
                "</div>";

        sendHtmlEmail(recipients, "👥 WARNING: Passenger Overload Alert - Vehicle Safety Monitor",
                getBaseEmailTemplate("PASSENGER OVERLOAD", "#d35400", "👥", content));
    }

    public void sendSignalBreakAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>📡 SIGNAL BREAK DETECTED!</p>" +
                "<p>Communication signal lost with the vehicle. GPS/Network connection interrupted.</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #8e44ad;'>Signal Information</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>📶 Signal Strength:</span><span class='sensor-value'>" + sensorData.get("signalStrength") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Last Known Location:</span><span class='sensor-value'>" + sensorData.get("lastKnownLocation") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>⚠️ RECOMMENDED ACTIONS:</strong><br>" +
                "1. Attempt to contact driver via phone<br>" +
                "2. Monitor for signal restoration<br>" +
                "3. Track last known GPS coordinates<br>" +
                "4. Activate backup communication protocols<br>" +
                "5. If prolonged, dispatch field team" +
                "</div>";

        sendHtmlEmail(recipients, "📡 WARNING: Signal Break Alert - Vehicle Safety Monitor",
                getBaseEmailTemplate("SIGNAL BREAK", "#8e44ad", "📡", content));
    }

    public void sendEmergencyAlert(String[] recipients, Map<String, String> sensorData) {
        String content = "<div class='alert-box'>" +
                "<p class='warning-text'>🚨 EMERGENCY ALERT!</p>" +
                "<p><strong>Emergency Type:</strong> " + sensorData.get("emergencyType") + "</p>" +
                "<p>" + sensorData.get("description") + "</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0; color: #c0392b;'>Emergency Details</h3>" +
                "<div class='sensor-row'><span class='sensor-label'>🚗 Vehicle ID:</span><span class='sensor-value'>" + sensorData.get("vehicleId") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>📍 Location:</span><span class='sensor-value'>" + sensorData.get("location") + "</span></div>" +
                "<div class='sensor-row'><span class='sensor-label'>⚠️ Type:</span><span class='sensor-value'>" + sensorData.get("emergencyType") + "</span></div>" +
                "</div>" +
                "<div class='action-required'>" +
                "<strong>🚨 IMMEDIATE ACTIONS REQUIRED:</strong><br>" +
                "1. Call emergency services (Police/Ambulance/Fire)<br>" +
                "2. Dispatch response team immediately<br>" +
                "3. Attempt to contact vehicle occupants<br>" +
                "4. Activate all emergency protocols<br>" +
                "5. Document all actions taken" +
                "</div>";

        sendHtmlEmail(recipients, "🚨 CRITICAL EMERGENCY: " + sensorData.get("emergencyType") + " - Vehicle Safety Monitor",
                getBaseEmailTemplate("EMERGENCY ALERT", "#c0392b", "🚨", content));
    }

    public void sendTestEmail(String[] recipients) {
        String content = "<div style='text-align: center; padding: 20px;'>" +
                "<h2 style='color: #27ae60;'>✅ Test Email Successful!</h2>" +
                "<p>Your Vehicle Safety Monitor email system is working correctly.</p>" +
                "<p>You will receive alerts at this email address when any safety event is detected.</p>" +
                "</div>" +
                "<div class='sensor-data'>" +
                "<h3 style='margin-top: 0;'>Available Alert Types:</h3>" +
                "<ul style='line-height: 2;'>" +
                "<li>🔥 Fire Alert</li>" +
                "<li>☣️ Gas Leak Alert</li>" +
                "<li>🍺 Alcohol Detection Alert</li>" +
                "<li>⚠️ Tilt/Accident Alert</li>" +
                "<li>👥 Passenger Overload Alert</li>" +
                "<li>📡 Signal Break Alert</li>" +
                "<li>🚨 Emergency Alert</li>" +
                "</ul>" +
                "</div>";

        sendHtmlEmail(recipients, "✅ Test Email - Vehicle Safety Monitor",
                getBaseEmailTemplate("TEST EMAIL", "#27ae60", "✅", content));
    }
}