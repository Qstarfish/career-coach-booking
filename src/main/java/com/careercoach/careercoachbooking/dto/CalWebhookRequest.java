package com.careercoach.careercoachbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Cal.com Webhook请求DTO
 * 根据实际webhook格式定义
 */
@Data
public class CalWebhookRequest {
    @JsonProperty("triggerEvent")
    private String triggerEvent;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("payload")
    private Payload payload;
    
    @Data
    public static class Payload {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("startTime")
        private String startTime;
        
        @JsonProperty("endTime")
        private String endTime;
        
        @JsonProperty("organizer")
        private Organizer organizer;
        
        @JsonProperty("attendees")
        private List<Attendee> attendees;
        
        @JsonProperty("uid")
        private String uid;
        
        @JsonProperty("bookingId")
        private Long bookingId;
        
        @JsonProperty("status")
        private String status;
    }
    
    @Data
    public static class Organizer {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
    }
    
    @Data
    public static class Attendee {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
    }
}