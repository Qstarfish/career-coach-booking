package com.careercoach.careercoachbooking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Consolidated configuration class for the demo project.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private Cal cal = new Cal();
    private Booking booking = new Booking();

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



    @Data
    public static class Cal {
        private Api api = new Api();
        private Webhook webhook = new Webhook();
        private String username;
        private String eventTypeSlug;
        private Integer eventTypeId;

        @Data
        public static class Api {
            private String url;
            private String key;
        }

        @Data
        public static class Webhook {
            private String secret;
        }
    }

    @Data
    public static class Booking {
        private String defaultTimeZone ;
        private String eventTypeSlug ;
        private String username ;
        private Integer durationMinutes ;
    }
}
