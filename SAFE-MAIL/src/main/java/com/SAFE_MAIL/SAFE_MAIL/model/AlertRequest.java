package com.SAFE_MAIL.SAFE_MAIL.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    private String alertType;
    private List<String> recipientEmails;
    private String vehicleId;
    private Double sensorValue;
    private String location;
    private String timestamp;
}