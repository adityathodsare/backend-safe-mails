package com.SAFE_MAIL.SAFE_MAIL.service;

import com.SAFE_MAIL.SAFE_MAIL.config.ThingSpeakConfig;
import com.SAFE_MAIL.SAFE_MAIL.dto.ThingSpeakResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThingSpeakService {

    private final ThingSpeakConfig config;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    // Track last processed entry IDs to avoid duplicate alerts
    private Long lastVehicleSafetyEntryId = 0L;
    private Long lastAlcoholMonitorEntryId = 0L;

    /**
     * Polls Vehicle Safety Monitor Channel (2897293) every minute
     */
    @Scheduled(fixedRateString = "${thingspeak.vehicle-safety.poll-interval-seconds:60}000")
    public void pollVehicleSafetyChannel() {
        try {
            String channelId = config.getVehicleSafety().getChannelId();
            String apiKey = config.getVehicleSafety().getReadApiKey();

            if (channelId == null || apiKey == null) {
                log.warn("Vehicle Safety channel not configured. Skipping...");
                return;
            }

            log.info("Polling Vehicle Safety Monitor Channel: {}", channelId);

            String url = String.format("%s/channels/%s/feeds.json?api_key=%s&results=1",
                    config.getBaseUrl(), channelId, apiKey);

            ThingSpeakResponse response = restTemplate.getForObject(url, ThingSpeakResponse.class);

            if (response != null && response.getFeeds() != null && !response.getFeeds().isEmpty()) {
                ThingSpeakResponse.Feed latestFeed = response.getFeeds().get(0);

                // Check if this is a new entry
                if (latestFeed.getEntryId() > lastVehicleSafetyEntryId) {
                    lastVehicleSafetyEntryId = latestFeed.getEntryId();
                    processVehicleSafetyData(latestFeed, response.getChannel());
                }
            }
        } catch (Exception e) {
            log.error("Error polling Vehicle Safety channel: {}", e.getMessage());
        }
    }

    /**
     * Polls Alcohol and Vehicle Monitor Channel (2898066) every minute
     */
    @Scheduled(fixedRateString = "${thingspeak.alcohol-monitor.poll-interval-seconds:60}000")
    public void pollAlcoholMonitorChannel() {
        try {
            String channelId = config.getAlcoholMonitor().getChannelId();
            String apiKey = config.getAlcoholMonitor().getReadApiKey();

            if (channelId == null || apiKey == null) {
                log.warn("Alcohol Monitor channel not configured. Skipping...");
                return;
            }

            log.info("Polling Alcohol Monitor Channel: {}", channelId);

            String url = String.format("%s/channels/%s/feeds.json?api_key=%s&results=1",
                    config.getBaseUrl(), channelId, apiKey);

            ThingSpeakResponse response = restTemplate.getForObject(url, ThingSpeakResponse.class);

            if (response != null && response.getFeeds() != null && !response.getFeeds().isEmpty()) {
                ThingSpeakResponse.Feed latestFeed = response.getFeeds().get(0);

                // Check if this is a new entry
                if (latestFeed.getEntryId() > lastAlcoholMonitorEntryId) {
                    lastAlcoholMonitorEntryId = latestFeed.getEntryId();
                    processAlcoholMonitorData(latestFeed, response.getChannel());
                }
            }
        } catch (Exception e) {
            log.error("Error polling Alcohol Monitor channel: {}", e.getMessage());
        }
    }

    /**
     * Process Vehicle Safety Monitor data and trigger alerts
     */
    private void processVehicleSafetyData(ThingSpeakResponse.Feed feed, ThingSpeakResponse.Channel channel) {
        log.info("Processing Vehicle Safety data - Entry ID: {}", feed.getEntryId());

        // Parse field values
        Double field1 = parseDouble(feed.getField1());
        Double field2 = parseDouble(feed.getField2());
        Double field3 = parseDouble(feed.getField3());
        Double field4 = parseDouble(feed.getField4());
        Double field5 = parseDouble(feed.getField5());

        String vehicleId = "VH-2897293";
        String location = getLocation(channel);
        String[] recipients = getAlertRecipients();

        // Field 1: Temperature (Fire Detection)
        if (field1 != null && field1 > config.getThresholds().getFireTemperature()) {
            log.warn("🔥 Fire alert triggered! Temperature: {}°C", field1);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("temperature", String.format("%.1f", field1));
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendFireAlert(recipients, sensorData);
        }

        // Field 2: Gas Level (Gas Leak Detection)
        if (field2 != null && field2 > config.getThresholds().getGasLevel()) {
            log.warn("☣️ Gas leak alert triggered! Level: {} PPM", field2);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("gasLevel", String.format("%.0f", field2));
            sensorData.put("gasType", "LPG/Combustible Gas");
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendGasLeakAlert(recipients, sensorData);
        }

        // Field 3: Tilt Angle (Accident Detection)
        if (field3 != null && field3 > config.getThresholds().getTiltAngle()) {
            log.warn("⚠️ Tilt/Accident alert triggered! Angle: {}°", field3);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("tiltAngle", String.format("%.1f", field3));
            sensorData.put("speed", field4 != null ? String.format("%.1f", field4) : "N/A");
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendTiltAccidentAlert(recipients, sensorData);
        }

        // Field 5: Signal Strength
        if (field5 != null && field5 < config.getThresholds().getSignalStrength()) {
            log.warn("📡 Signal break alert triggered! Strength: {}%", field5);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("signalStrength", String.format("%.0f%%", field5));
            sensorData.put("lastKnownLocation", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendSignalBreakAlert(recipients, sensorData);
        }
    }

    /**
     * Process Alcohol Monitor data and trigger alerts
     */
    private void processAlcoholMonitorData(ThingSpeakResponse.Feed feed, ThingSpeakResponse.Channel channel) {
        log.info("Processing Alcohol Monitor data - Entry ID: {}", feed.getEntryId());

        // Parse field values
        Double field1 = parseDouble(feed.getField1());
        Double field2 = parseDouble(feed.getField2());
        Double field3 = parseDouble(feed.getField3());
        Double field4 = parseDouble(feed.getField4());
        Double field5 = parseDouble(feed.getField5());
        Double field6 = parseDouble(feed.getField6());

        String vehicleId = "VH-2898066";
        String location = getLocation(channel);
        String[] recipients = getAlertRecipients();

        // Field 1: Alcohol Level
        if (field1 != null && field1 > config.getThresholds().getAlcoholLevel()) {
            log.warn("🍺 Alcohol alert triggered! Level: {} mg/L", field1);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("alcoholLevel", String.format("%.2f", field1));
            sensorData.put("driverName", "Driver-" + vehicleId);
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendAlcoholAlert(recipients, sensorData);
        }

        // Field 2: Temperature
        if (field2 != null && field2 > config.getThresholds().getFireTemperature()) {
            log.warn("🔥 Fire alert triggered! Temperature: {}°C", field2);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("temperature", String.format("%.1f", field2));
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendFireAlert(recipients, sensorData);
        }

        // Field 3: Gas Level
        if (field3 != null && field3 > config.getThresholds().getGasLevel()) {
            log.warn("☣️ Gas leak alert triggered! Level: {} PPM", field3);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("gasLevel", String.format("%.0f", field3));
            sensorData.put("gasType", "MQ Series Sensor");
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendGasLeakAlert(recipients, sensorData);
        }

        // Field 4: Tilt Angle
        if (field4 != null && field4 > config.getThresholds().getTiltAngle()) {
            log.warn("⚠️ Tilt/Accident alert triggered! Angle: {}°", field4);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("tiltAngle", String.format("%.1f", field4));
            sensorData.put("speed", field5 != null ? String.format("%.1f", field5) : "N/A");
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendTiltAccidentAlert(recipients, sensorData);
        }

        // Field 6: Passenger Count
        if (field6 != null && field6 > config.getThresholds().getPassengerOverload()) {
            log.warn("👥 Passenger overload alert triggered! Count: {}", field6);
            Map<String, String> sensorData = new HashMap<>();
            sensorData.put("passengerCount", String.format("%.0f", field6));
            sensorData.put("maxCapacity", String.valueOf(config.getThresholds().getPassengerOverload()));
            sensorData.put("location", location);
            sensorData.put("vehicleId", vehicleId);
            sensorData.put("source", "ThingSpeak Auto-Monitor");
            emailService.sendPassengerOverloadAlert(recipients, sensorData);
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getLocation(ThingSpeakResponse.Channel channel) {
        if (channel.getLatitude() != null && channel.getLongitude() != null) {
            return String.format("Lat:%s, Lon:%s", channel.getLatitude(), channel.getLongitude());
        }
        return "Location Unknown";
    }

    private String[] getAlertRecipients() {
        // TODO: Make this configurable
        return new String[]{"adityathodsare45@gmail.com"};
    }

    /**
     * Manual fetch for testing
     */
    public ThingSpeakResponse fetchChannelData(String channelId, String apiKey, int results) {
        String url = String.format("%s/channels/%s/feeds.json?api_key=%s&results=%d",
                config.getBaseUrl(), channelId, apiKey, results);
        return restTemplate.getForObject(url, ThingSpeakResponse.class);
    }
}