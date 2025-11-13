package com.SAFE_MAIL.SAFE_MAIL.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class ApiGeneratorService {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.iot.default-device:ESP32_001}")
    private String defaultDeviceId;

    // Generate ESP32-friendly API URL
    public String generateIotApiUrl(String templateName, Map<String, String> variables) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl).append("/email/iot-alert?");
        urlBuilder.append("alertType=").append(URLEncoder.encode(templateName, StandardCharsets.UTF_8));
        urlBuilder.append("&deviceId=").append(URLEncoder.encode("{{DEVICE_ID}}", StandardCharsets.UTF_8));

        // Add template variables as parameters
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                if (!entry.getKey().equals("deviceId")) { // deviceId is already added
                    urlBuilder.append("&additionalData=")
                            .append(URLEncoder.encode(entry.getKey() + ":" + "{{VALUE}}", StandardCharsets.UTF_8));
                }
            }
        }

        return urlBuilder.toString();
    }

    // Generate simple API URL for ESP32
    public String generateSimpleApiUrl(String to, String subject, String message) {
        String encodedTo = URLEncoder.encode(to, StandardCharsets.UTF_8);
        String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8);
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

        return String.format("%s/email/send-simple?to=%s&subject=%s&message=%s",
                baseUrl, encodedTo, encodedSubject, encodedMessage);
    }

    public String generateApiUrl(List<String> receivers, String messageBody) {
        String receiversParam = String.join(",", receivers);
        String encodedReceivers = URLEncoder.encode(receiversParam, StandardCharsets.UTF_8);
        String encodedMessage = URLEncoder.encode(messageBody, StandardCharsets.UTF_8);

        return String.format("%s/email/send?receivers=%s&message=%s",
                baseUrl, encodedReceivers, encodedMessage);
    }

    public String generateApiUrlWithSubject(List<String> receivers, String subject, String messageBody) {
        String receiversParam = String.join(",", receivers);
        String encodedReceivers = URLEncoder.encode(receiversParam, StandardCharsets.UTF_8);
        String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8);
        String encodedMessage = URLEncoder.encode(messageBody, StandardCharsets.UTF_8);

        return String.format("%s/email/send?receivers=%s&subject=%s&message=%s",
                baseUrl, encodedReceivers, encodedSubject, encodedMessage);
    }
}