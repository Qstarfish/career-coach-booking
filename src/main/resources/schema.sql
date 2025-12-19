-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '全局唯一用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '用户姓名',
    `email` VARCHAR(255) NOT NULL COMMENT '用户邮箱',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 预约表
CREATE TABLE IF NOT EXISTS `booking` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `cal_booking_id` BIGINT COMMENT 'Cal.com预约ID',
    `booking_uid` VARCHAR(100) COMMENT 'Cal.com预约UID',
    `coach_name` VARCHAR(100) COMMENT '导师名称',
    `coach_email` VARCHAR(255) COMMENT '导师邮箱',
    `start_time` DATETIME NOT NULL COMMENT '预约开始时间',
    `end_time` DATETIME NOT NULL COMMENT '预约结束时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '预约状态',
    `cal_event_type_id` BIGINT COMMENT 'Cal.com事件类型ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_cal_booking_id` (`cal_booking_id`),
    INDEX `idx_booking_uid` (`booking_uid`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

