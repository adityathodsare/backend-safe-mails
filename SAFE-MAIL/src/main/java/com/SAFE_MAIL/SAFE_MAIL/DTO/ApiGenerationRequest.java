package com.SAFE_MAIL.SAFE_MAIL.DTO;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ApiGenerationRequest {
    private String templateName;
    private List<String> receivers;
    private String subject;
    private Map<String, String> variables;

    // For IoT API generation
    private boolean iotFriendly = false;
    private String deviceIdPlaceholder = "ESP32_001";
}