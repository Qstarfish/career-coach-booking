package com.careercoach.careercoachbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Cal.com Webhook事件DTO
 */
@Data
public class CalWebhookEvent {
    @JsonProperty("triggerEvent")
    private String triggerEvent;
    
    @JsonProperty("payload")
    private CalWebhookPayload payload;
    
    @Data
    public static class CalWebhookPayload {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("startTime")
        private String startTime;
        
        @JsonProperty("endTime")
        private String endTime;
        
        @JsonProperty("attendees")
        private Attendee[] attendees;
        
        @JsonProperty("eventTypeId")
        private Long eventTypeId;
        
        @JsonProperty("status")
        private String status;
        
        @Data
        public static class Attendee {
            @JsonProperty("name")
            private String name;
            
            @JsonProperty("email")
            private String email;
        }
    }
}

