package com.careercoach.careercoachbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Cal.com 创建预约响应DTO
 */
//@Data
//public class CalCreateBookingResponse {
//    @JsonProperty("booking")
//    private Booking booking;
//
//
//    @Data
//    public static class Booking {
//        @JsonProperty("id")
//        private Long id;
//
//        @JsonProperty("uid")
//        private String uid;
//
//        @JsonProperty("title")
//        private String title;
//
//        @JsonProperty("startTime")
//        private String startTime;
//
//        @JsonProperty("endTime")
//        private String endTime;
//
//        @JsonProperty("status")
//        private String status;
//
//        @JsonProperty("eventTypeId")
//        private Long eventTypeId;
//
//        @JsonProperty("paymentUid")
//        private String paymentUid;
//
//        @JsonProperty("paid")
//        private Boolean paid;
//    }
//}

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalCreateBookingResponse {

    private String status;
    private BookingData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookingData {
        private Long id;
        private String uid;                    // ✅ 用于生成 URL 的关键字段
        private String title;
        private String description;
        private String status;
        private String start;                  // ✅ 不是 startTime
        private String end;
        private Integer duration;
        private Integer eventTypeId;
        private EventType eventType;
        private String location;
        private String meetingUrl;             // ✅ 会议链接
        private String createdAt;
        private String updatedAt;
        private List<Host> hosts;
        private List<Attendee> attendees;
        private Map<String, Object> bookingFieldsResponses;
        private Map<String, Object> metadata;
        private String cancellationReason;
        private String reschedulingReason;
        private String rescheduledFromUid;
        private String rescheduledToUid;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventType {
        private Integer id;
        private String slug;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attendee {
        private String name;
        private String email;
        private String timeZone;
        private String language;
        private String phoneNumber;
        private Boolean absent;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Host {
        private Long id;
        private String name;
        private String email;
        private String username;
        private String timeZone;
    }
}