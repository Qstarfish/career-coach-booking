package com.careercoach.careercoachbooking.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
public class Booking {
    private Long id;
    private String userId;
    private Long calBookingId;
    private String coachName;
    private String coachEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long calEventTypeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bookingUid;
}

