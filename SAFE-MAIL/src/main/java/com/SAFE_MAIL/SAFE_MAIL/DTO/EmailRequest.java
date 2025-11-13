package com.SAFE_MAIL.SAFE_MAIL.DTO;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String message;
    private boolean isHtml = true;

    // For multiple recipients
    private List<String> receivers;

    // For template processing
    private String templateName;
    private Map<String, String> variables;

    // For IoT applications
    private String deviceId;
    private String alertType;
    private String additionalData;

    // Constructors
    public EmailRequest() {}

    public EmailRequest(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public EmailRequest(String to, String subject, String message, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.isHtml = isHtml;
    }
}