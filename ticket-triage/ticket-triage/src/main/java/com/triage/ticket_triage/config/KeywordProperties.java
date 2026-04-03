package com.triage.ticket_triage.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "categories")
public class KeywordProperties {

    // Maps category name → its config (keywords list)
    private Map<String, CategoryConfig> categories;
    private UrgencyConfig urgency;

    @Data
    public static class CategoryConfig {
        private List<String> keywords;
    }

    @Data
    public static class UrgencyConfig {
        private List<String> keywords;
    }
}
