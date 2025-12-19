package com.careercoach.careercoachbooking.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约信息响应DTO
 */
@Data
public class BookingResponse {
    private Long id;
    private String status;
    private String coachName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

