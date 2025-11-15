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

    @GetMapping("/fire")
    public ResponseEntity<Map<String, String>> sendFireAlert(
            @RequestParam(required = false) String temperature,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("temperature", temperature != null ? temperature : "N/A");
        sensorData.put("location", location != null ? location : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendFireAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Fire alert sent successfully"));
    }

    @GetMapping("/gas-leak")
    public ResponseEntity<Map<String, String>> sendGasLeakAlert(
            @RequestParam(required = false) String gasLevel,
            @RequestParam(required = false) String gasType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("gasLevel", gasLevel != null ? gasLevel : "N/A");
        sensorData.put("gasType", gasType != null ? gasType : "N/A");
        sensorData.put("location", location != null ? location : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendGasLeakAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Gas leak alert sent successfully"));
    }

    @GetMapping("/alcohol")
    public ResponseEntity<Map<String, String>> sendAlcoholAlert(
            @RequestParam(required = false) String alcoholLevel,
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("alcoholLevel", alcoholLevel != null ? alcoholLevel : "N/A");
        sensorData.put("driverName", driverName != null ? driverName : "Unknown");
        sensorData.put("location", location != null ? location : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendAlcoholAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Alcohol alert sent successfully"));
    }

    @GetMapping("/tilt-accident")
    public ResponseEntity<Map<String, String>> sendTiltAccidentAlert(
            @RequestParam(required = false) String tiltAngle,
            @RequestParam(required = false) String speed,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("tiltAngle", tiltAngle != null ? tiltAngle : "N/A");
        sensorData.put("speed", speed != null ? speed : "N/A");
        sensorData.put("location", location != null ? location : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendTiltAccidentAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Tilt/Accident alert sent successfully"));
    }

    @GetMapping("/passenger-overload")
    public ResponseEntity<Map<String, String>> sendPassengerOverloadAlert(
            @RequestParam(required = false) String passengerCount,
            @RequestParam(required = false) String maxCapacity,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("passengerCount", passengerCount != null ? passengerCount : "N/A");
        sensorData.put("maxCapacity", maxCapacity != null ? maxCapacity : "N/A");
        sensorData.put("location", location != null ? location : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendPassengerOverloadAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Passenger overload alert sent successfully"));
    }

    @GetMapping("/signal-break")
    public ResponseEntity<Map<String, String>> sendSignalBreakAlert(
            @RequestParam(required = false) String signalStrength,
            @RequestParam(required = false) String lastKnownLocation,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("signalStrength", signalStrength != null ? signalStrength : "N/A");
        sensorData.put("lastKnownLocation", lastKnownLocation != null ? lastKnownLocation : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendSignalBreakAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Signal break alert sent successfully"));
    }

    @GetMapping("/emergency")
    public ResponseEntity<Map<String, String>> sendEmergencyAlert(
            @RequestParam String emergencyType,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String vehicleId,
            @RequestParam String recipients) {

        Map<String, String> sensorData = new HashMap<>();
        sensorData.put("emergencyType", emergencyType);
        sensorData.put("description", description != null ? description : "N/A");
        sensorData.put("location", location != null ? location : "N/A");
        sensorData.put("vehicleId", vehicleId != null ? vehicleId : "Unknown");

        emailService.sendEmergencyAlert(recipients.split(","), sensorData);
        return ResponseEntity.ok(Map.of("status", "Emergency alert sent successfully"));
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEmail(
            @RequestParam String recipients) {
        emailService.sendTestEmail(recipients.split(","));
        return ResponseEntity.ok(Map.of("status", "Test email sent successfully"));
    }
}