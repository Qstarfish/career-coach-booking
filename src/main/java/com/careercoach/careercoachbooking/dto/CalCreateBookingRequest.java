package com.careercoach.careercoachbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Cal.com 创建预约请求DTO
 * 参考: https://cal.com/docs/api-reference/v2/bookings/create-a-booking
 */
@Data
public class CalCreateBookingRequest {
    @JsonProperty("eventTypeSlug")
    private String eventTypeSlug;

    @JsonProperty("start")
    private String start;

    @JsonProperty("username")
    private String username;

    @JsonProperty("eventTypeId")
    private Integer eventTypeId;
    
    @JsonProperty("attendee")
    private Attendee attendee;
    
//    @JsonProperty("responses")
//    private BookingResponses responses;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata = new HashMap<>();
    
    @Data
    public static class Attendee {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("timeZone")
        private String timeZone;
        
        @JsonProperty("language")
        private String language;
    }
    
//    @Data
//    public static class BookingResponses {
//        @JsonProperty("notes")
//        private String notes = ""; // Cal.com API要求此字段必须存在
//    }
}
