package com.SAFE_MAIL.SAFE_MAIL.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "thingspeak")
@Data
public class ThingSpeakConfig {
    private String baseUrl = "https://api.thingspeak.com";
    private Channel vehicleSafety = new Channel();
    private Channel alcoholMonitor = new Channel();

    @Data
    public static class Channel {
        private String channelId;
        private String readApiKey;
        private int pollIntervalSeconds = 60;
    }

    // Alert Thresholds
    private Thresholds thresholds = new Thresholds();

    @Data
    public static class Thresholds {
        private double fireTemperature = 80.0;
        private int gasLevel = 400;
        private double alcoholLevel = 0.3;
        private double tiltAngle = 30.0;
        private int passengerOverload = 50;
        private int signalStrength = 10;
    }
}