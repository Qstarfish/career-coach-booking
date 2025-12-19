package com.careercoach.careercoachbooking.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

