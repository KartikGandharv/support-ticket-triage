package com.triage.ticket_triage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.triage.ticket_triage.config.KeywordProperties;

@SpringBootApplication
@EnableConfigurationProperties(KeywordProperties.class)
public class TicketTriageApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketTriageApplication.class, args);
    }
}
