package com.careercoach.careercoachbooking.entity;

/**
 * 预约状态枚举
 */
public enum BookingStatus {
    PENDING("PENDING", "初始状态"),
    BOOKING_CREATED("BOOKING_CREATED", "支付成功且预约确认"),
    BOOKING_CANCELLED("BOOKING_CANCELLED", "预约已取消"),
    MEETING_ENDED("MEETING_ENDED", "课程正常结束"),
    NO_SHOW("NO_SHOW", "用户或导师未出席");

    private final String code;
    private final String description;

    BookingStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static BookingStatus fromCode(String code) {
        for (BookingStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的预约状态: " + code);
    }
}

