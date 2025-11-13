package com.SAFE_MAIL.SAFE_MAIL.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class TemplateService {

    private final Map<String, String> templates = new HashMap<>();

    public TemplateService() {
        // Predefined templates
        templates.put("welcome",
                "<h1>Welcome {{name}}!</h1>" +
                        "<p>Thank you for joining us. Your account was created on {{date}}.</p>" +
                        "<p>We're excited to have you on board!</p>");

        templates.put("notification",
                "<div style='background: #f5f5f5; padding: 20px;'>" +
                        "<h2>Notification: {{title}}</h2>" +
                        "<p>{{message}}</p>" +
                        "<p><strong>Time:</strong> {{time}}</p>" +
                        "</div>");

        templates.put("invitation",
                "<h1>You're Invited!</h1>" +
                        "<p>Dear {{friend_name}},</p>" +
                        "<p>{{user_name}} has invited you to join our platform.</p>" +
                        "<p>Click here to accept: {{link}}</p>");

        // IoT Specific Templates
        templates.put("accident",
                "<div style='background: #ffebee; padding: 20px; border-left: 4px solid #f44336;'>" +
                        "<h1 style='color: #d32f2f;'>🚨 CAR ACCIDENT DETECTED</h1>" +
                        "<p><strong>Device ID:</strong> {{deviceId}}</p>" +
                        "<p><strong>Time:</strong> {{timestamp}}</p>" +
                        "<p><strong>Additional Data:</strong> {{additionalData}}</p>" +
                        "<p><strong>Action Required:</strong> Check vehicle stability and notify emergency services immediately.</p>" +
                        "</div>");

        templates.put("gas-leak",
                "<div style='background: #fff3e0; padding: 20px; border-left: 4px solid #ff9800;'>" +
                        "<h1 style='color: #ef6c00;'>⚠️ GAS LEAK DETECTED</h1>" +
                        "<p><strong>Device ID:</strong> {{deviceId}}</p>" +
                        "<p><strong>Time:</strong> {{timestamp}}</p>" +
                        "<p><strong>Additional Data:</strong> {{additionalData}}</p>" +
                        "<p><strong>Action Required:</strong> Evacuate the area and take safety measures.</p>" +
                        "</div>");

        templates.put("alcohol-detected",
                "<div style='background: #fce4ec; padding: 20px; border-left: 4px solid #e91e63;'>" +
                        "<h1 style='color: #ad1457;'>🚫 ALCOHOL DETECTED</h1>" +
                        "<p><strong>Device ID:</strong> {{deviceId}}</p>" +
                        "<p><strong>Time:</strong> {{timestamp}}</p>" +
                        "<p><strong>Additional Data:</strong> {{additionalData}}</p>" +
                        "<p><strong>Action Required:</strong> Engine will remain OFF for safety. Driver is impaired.</p>" +
                        "</div>");

        templates.put("fire-detected",
                "<div style='background: #fff8e1; padding: 20px; border-left: 4px solid #ff6f00;'>" +
                        "<h1 style='color: #e65100;'>🔥 FIRE DETECTED</h1>" +
                        "<p><strong>Device ID:</strong> {{deviceId}}</p>" +
                        "<p><strong>Time:</strong> {{timestamp}}</p>" +
                        "<p><strong>Additional Data:</strong> {{additionalData}}</p>" +
                        "<p><strong>Action Required:</strong> Use fire extinguisher and evacuate immediately.</p>" +
                        "</div>");

        templates.put("temperature-alert",
                "<div style='background: #e3f2fd; padding: 20px; border-left: 4px solid #2196f3;'>" +
                        "<h1 style='color: #1565c0;'>🌡️ TEMPERATURE ALERT</h1>" +
                        "<p><strong>Device ID:</strong> {{deviceId}}</p>" +
                        "<p><strong>Time:</strong> {{timestamp}}</p>" +
                        "<p><strong>Additional Data:</strong> {{additionalData}}</p>" +
                        "<p><strong>Action Required:</strong> Temperature threshold exceeded.</p>" +
                        "</div>");
    }

    public String processTemplate(String templateName, Map<String, String> variables) {
        String template = templates.get(templateName);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }

        String processed = template;
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                processed = processed.replace(placeholder,
                        entry.getValue() != null ? entry.getValue() : "");
            }
        }

        return processed;
    }

    public Map<String, String> getAvailableTemplates() {
        return new HashMap<>(templates);
    }

    public void addTemplate(String name, String template) {
        templates.put(name, template);
    }
}