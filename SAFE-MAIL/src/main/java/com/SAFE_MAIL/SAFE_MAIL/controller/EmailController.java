package com.SAFE_MAIL.SAFE_MAIL.controller;

import com.SAFE_MAIL.SAFE_MAIL.DTO.EmailRequest;
import com.SAFE_MAIL.SAFE_MAIL.DTO.ApiGenerationRequest;
import com.SAFE_MAIL.SAFE_MAIL.service.EmailService;
import com.SAFE_MAIL.SAFE_MAIL.service.TemplateService;
import com.SAFE_MAIL.SAFE_MAIL.service.ApiGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final TemplateService templateService;
    private final ApiGeneratorService apiGeneratorService;

    // ✅ SIMPLIFIED ESP32 ENDPOINT - Perfect for IoT devices
    @GetMapping("/send-simple")
    public ResponseEntity<Map<String, String>> sendEmailSimple(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam(required = false) String template) {

        try {
            Map<String, String> response = new HashMap<>();

            String finalMessage = message;
            if (template != null && !template.trim().isEmpty()) {
                // Use template if provided
                finalMessage = templateService.processTemplate(template, new HashMap<>());
            }

            emailService.sendHtmlEmail(to, subject, finalMessage);

            response.put("status", "success");
            response.put("message", "Email sent successfully");
            response.put("to", to);
            response.put("subject", subject);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ✅ IOT ALERT ENDPOINT - Simplified for ESP32
    @GetMapping("/iot-alert")
    public ResponseEntity<Map<String, String>> sendIotAlert(
            @RequestParam String alertType,
            @RequestParam String deviceId,
            @RequestParam(required = false) String additionalData) {

        try {
            Map<String, String> response = new HashMap<>();

            // Process alert template
            Map<String, String> variables = new HashMap<>();
            variables.put("deviceId", deviceId);
            variables.put("additionalData", additionalData != null ? additionalData : "No additional data");
            variables.put("timestamp", java.time.LocalDateTime.now().toString());

            String processedMessage = templateService.processTemplate(alertType, variables);
            String subject = "🚨 IOT Alert: " + alertType.toUpperCase() + " - Device: " + deviceId;

            // Send to predefined recipients (configure in application.properties)
            emailService.sendIotAlertEmail(subject, processedMessage);

            response.put("status", "success");
            response.put("message", "IOT alert sent successfully");
            response.put("alertType", alertType);
            response.put("deviceId", deviceId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to send IOT alert: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ✅ MAIN ENDPOINT: Send to multiple receivers via URL params
    @PostMapping("/send")
    public ResponseEntity<String> sendEmailToMultiple(
            @RequestParam String receivers,
            @RequestParam(required = false, defaultValue = "Notification") String subject,
            @RequestParam String message) {

        try {
            List<String> receiverList = Arrays.asList(receivers.split(","));
            emailService.sendHtmlEmailToMultiple(receiverList, subject, message);
            return ResponseEntity.ok("✅ Email sent successfully to " + receiverList.size() + " recipients");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("❌ Failed to send email: " + e.getMessage());
        }
    }

    // ✅ ALTERNATIVE ENDPOINT: Send email via JSON body
    @PostMapping("/send-json")
    public ResponseEntity<Map<String, Object>> sendEmailJson(@RequestBody EmailRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
                // Multiple receivers
                emailService.sendHtmlEmailToMultiple(request.getReceivers(), request.getSubject(), request.getMessage());
                response.put("status", "success");
                response.put("message", "Email sent successfully to " + request.getReceivers().size() + " recipients");
                response.put("recipientsCount", request.getReceivers().size());
            } else {
                // Single receiver
                if (request.isHtml()) {
                    emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getMessage());
                } else {
                    emailService.sendTextEmail(request.getTo(), request.getSubject(), request.getMessage());
                }
                response.put("status", "success");
                response.put("message", "Email sent successfully to " + request.getTo());
                response.put("recipient", request.getTo());
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ TEMPLATE ENDPOINT: Send email using template
    @PostMapping("/send-template")
    public ResponseEntity<Map<String, Object>> sendEmailWithTemplate(@RequestBody EmailRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (request.getTemplateName() == null) {
                response.put("status", "error");
                response.put("message", "Template name is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Process template
            String processedMessage = templateService.processTemplate(
                    request.getTemplateName(),
                    request.getVariables() != null ? request.getVariables() : new HashMap<>()
            );

            // Send email
            if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
                emailService.sendHtmlEmailToMultiple(request.getReceivers(), request.getSubject(), processedMessage);
                response.put("status", "success");
                response.put("message", "Template email sent to " + request.getReceivers().size() + " recipients");
                response.put("recipientsCount", request.getReceivers().size());
            } else {
                emailService.sendHtmlEmail(request.getTo(), request.getSubject(), processedMessage);
                response.put("status", "success");
                response.put("message", "Template email sent to " + request.getTo());
                response.put("recipient", request.getTo());
            }
            response.put("templateUsed", request.getTemplateName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send template email: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ API GENERATOR: Generate API URL for ESP32
    @PostMapping("/generate-iot-api")
    public ResponseEntity<Map<String, String>> generateIotApiUrl(@RequestBody ApiGenerationRequest request) {
        try {
            // Validate input
            if (request.getTemplateName() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Template name is required"));
            }

            // Generate ESP32-friendly API URL
            String apiUrl = apiGeneratorService.generateIotApiUrl(
                    request.getTemplateName(),
                    request.getVariables() != null ? request.getVariables() : new HashMap<>()
            );

            return ResponseEntity.ok(Map.of(
                    "apiUrl", apiUrl,
                    "usage", "Call this URL from ESP32 when alert is detected",
                    "method", "GET",
                    "template", request.getTemplateName(),
                    "example", "curl \"" + apiUrl + "&deviceId=ESP32_001&additionalData=temperature_high\""
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to generate API: " + e.getMessage()));
        }
    }

    // ✅ API GENERATOR: Generate API URL for user to copy
    @PostMapping("/generate-api")
    public ResponseEntity<Map<String, String>> generateApiUrl(@RequestBody ApiGenerationRequest request) {
        try {
            if (request.getReceivers() == null || request.getReceivers().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Receivers list cannot be empty"));
            }

            if (request.getTemplateName() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Template name is required"));
            }

            // Process template
            String processedMessage = templateService.processTemplate(
                    request.getTemplateName(),
                    request.getVariables() != null ? request.getVariables() : new HashMap<>()
            );

            // Generate API URL
            String apiUrl = apiGeneratorService.generateApiUrlWithSubject(
                    request.getReceivers(),
                    request.getSubject() != null ? request.getSubject() : "Notification",
                    processedMessage
            );

            return ResponseEntity.ok(Map.of(
                    "apiUrl", apiUrl,
                    "preview", processedMessage.length() > 500 ?
                            processedMessage.substring(0, 500) + "..." : processedMessage,
                    "message", "Copy this API URL and use it in your applications",
                    "receiversCount", String.valueOf(request.getReceivers().size())
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to generate API: " + e.getMessage()));
        }
    }

    // ✅ TEMPLATE MANAGEMENT: Get available templates
    @GetMapping("/templates")
    public ResponseEntity<Map<String, String>> getTemplates() {
        return ResponseEntity.ok(templateService.getAvailableTemplates());
    }

    // ✅ Health check with system info
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("service", "Email API Generator Service");
        status.put("timestamp", java.time.LocalDateTime.now().toString());
        status.put("iotReady", true);
        status.put("endpoints", List.of(
                "/email/send-simple (GET) - For ESP32",
                "/email/iot-alert (GET) - For IOT alerts",
                "/email/generate-iot-api (POST) - Generate ESP32 APIs"
        ));
        return ResponseEntity.ok(status);
    }
}