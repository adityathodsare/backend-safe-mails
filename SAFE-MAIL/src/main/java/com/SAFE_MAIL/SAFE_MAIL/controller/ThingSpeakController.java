package com.SAFE_MAIL.SAFE_MAIL.controller;

import com.SAFE_MAIL.SAFE_MAIL.dto.ThingSpeakResponse;
import com.SAFE_MAIL.SAFE_MAIL.service.ThingSpeakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/thingspeak")
@RequiredArgsConstructor
public class ThingSpeakController {

    private final ThingSpeakService thingSpeakService;

    /**
     * Manually trigger Vehicle Safety Monitor polling
     */
    @GetMapping("/poll/vehicle-safety")
    public ResponseEntity<Map<String, String>> pollVehicleSafety() {
        thingSpeakService.pollVehicleSafetyChannel();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Vehicle Safety Monitor channel polled successfully"
        ));
    }

    /**
     * Manually trigger Alcohol Monitor polling
     */
    @GetMapping("/poll/alcohol-monitor")
    public ResponseEntity<Map<String, String>> pollAlcoholMonitor() {
        thingSpeakService.pollAlcoholMonitorChannel();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Alcohol Monitor channel polled successfully"
        ));
    }

    /**
     * Fetch raw ThingSpeak data for testing
     */
    @GetMapping("/fetch")
    public ResponseEntity<ThingSpeakResponse> fetchChannelData(
            @RequestParam String channelId,
            @RequestParam String apiKey,
            @RequestParam(defaultValue = "10") int results) {

        ThingSpeakResponse response = thingSpeakService.fetchChannelData(channelId, apiKey, results);
        return ResponseEntity.ok(response);
    }

    /**
     * Get monitoring status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMonitoringStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("message", "ThingSpeak auto-monitoring is active");
        status.put("channels", Map.of(
                "vehicleSafety", "Channel 2897293 - Polling every 60 seconds",
                "alcoholMonitor", "Channel 2898066 - Polling every 60 seconds"
        ));
        return ResponseEntity.ok(status);
    }
}