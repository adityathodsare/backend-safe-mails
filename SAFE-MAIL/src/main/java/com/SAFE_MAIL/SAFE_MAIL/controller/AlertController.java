package com.SAFE_MAIL.SAFE_MAIL.controller;

import com.SAFE_MAIL.SAFE_MAIL.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final EmailService emailService;

    // 1. Alcohol Detected Alert
    @GetMapping("/alcohol")
    public ResponseEntity<Map<String, String>> sendAlcoholAlert(
            @RequestParam String recipients,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String location) {

        Map<String, String> alertData = new HashMap<>();
        alertData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown Vehicle");
        alertData.put("location", location != null ? location : "Location not available");

        emailService.sendAlcoholAlert(recipients.split(","), alertData);
        return ResponseEntity.ok(Map.of("status", "Alcohol alert sent successfully"));
    }

    // 2. Accident Detected Alert
    @GetMapping("/accident")
    public ResponseEntity<Map<String, String>> sendAccidentAlert(
            @RequestParam String recipients,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String location) {

        Map<String, String> alertData = new HashMap<>();
        alertData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown Vehicle");
        alertData.put("location", location != null ? location : "Location not available");

        emailService.sendAccidentAlert(recipients.split(","), alertData);
        return ResponseEntity.ok(Map.of("status", "Accident alert sent successfully"));
    }

    // 3. Fire Detected Alert
    @GetMapping("/fire")
    public ResponseEntity<Map<String, String>> sendFireAlert(
            @RequestParam String recipients,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String location) {

        Map<String, String> alertData = new HashMap<>();
        alertData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown Vehicle");
        alertData.put("location", location != null ? location : "Location not available");

        emailService.sendFireAlert(recipients.split(","), alertData);
        return ResponseEntity.ok(Map.of("status", "Fire alert sent successfully"));
    }

    // 4. Gas CNG Leak Alert
    @GetMapping("/gas-leak")
    public ResponseEntity<Map<String, String>> sendGasLeakAlert(
            @RequestParam String recipients,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String location) {

        Map<String, String> alertData = new HashMap<>();
        alertData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown Vehicle");
        alertData.put("location", location != null ? location : "Location not available");

        emailService.sendGasLeakAlert(recipients.split(","), alertData);
        return ResponseEntity.ok(Map.of("status", "Gas leak alert sent successfully"));
    }

    // 5. Temperature Warning Alert
    @GetMapping("/temperature")
    public ResponseEntity<Map<String, String>> sendTemperatureAlert(
            @RequestParam String recipients,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String location) {

        Map<String, String> alertData = new HashMap<>();
        alertData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown Vehicle");
        alertData.put("location", location != null ? location : "Location not available");

        emailService.sendTemperatureAlert(recipients.split(","), alertData);
        return ResponseEntity.ok(Map.of("status", "Temperature alert sent successfully"));
    }

    // 6. Order Confirmation Alert
    @GetMapping("/confirmation")
    public ResponseEntity<Map<String, String>> sendConfirmationAlert(
            @RequestParam String recipients,
            @RequestParam(required = false) String vehicleId) {

        Map<String, String> alertData = new HashMap<>();
        alertData.put("vehicleId", vehicleId != null ? vehicleId : "Your Vehicle");

        emailService.sendConfirmationAlert(recipients.split(","), alertData);
        return ResponseEntity.ok(Map.of("status", "Confirmation alert sent successfully"));
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEmail(@RequestParam String recipients) {
        emailService.sendTestEmail(recipients.split(","));
        return ResponseEntity.ok(Map.of("status", "Test email sent successfully"));
    }
}