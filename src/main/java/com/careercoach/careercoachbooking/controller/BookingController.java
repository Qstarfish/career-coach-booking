package com.careercoach.careercoachbooking.controller;

import com.careercoach.careercoachbooking.dto.BookingResponse;
import com.careercoach.careercoachbooking.dto.BookingUrlResponse;
import com.careercoach.careercoachbooking.entity.Booking;
import com.careercoach.careercoachbooking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约控制器
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    /**
     * 功能A: "去预约" 列表 - 创建预约并返回真实可预约的URL
     * POST /api/booking-url
     * 
     * 请求参数:
     * - userId: 用户ID
     * 
     * 其他参数（时区、eventTypeId等）从配置类读取，避免硬编码
     */
    @PostMapping("/booking-url")
    public ResponseEntity<?> getBookingUrl(@RequestParam String userId) {
        log.info("功能A: 获取预约URL - userId={}", userId);
        
        try {
            String bookingUrl = bookingService.createBookingAndGetUrl(userId);
            BookingUrlResponse response = new BookingUrlResponse();
            response.setBookingUrl(bookingUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("功能A: 获取预约URL失败 - {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * 功能B: "我的预约" 列表 - 查询数据库中已落库的预约记录
     * GET /api/bookings?userId=xxx
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@RequestParam String userId) {
        log.info("功能B: 查询预约列表 - userId={}", userId);
        List<BookingResponse> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * 功能C: "去取消预约" - 返回真实可取消预约的URL
     * POST /api/bookings/cancel
     * 
     * 请求参数:
     * - userId: 用户ID（用于验证预约归属）
     * - bookingId: 数据库中的预约ID
     * 
     * 返回取消预约的URL，用户访问该URL后，Cal.com会通过webhook推送BOOKING_CANCELLED事件更新数据库
     */
    @PostMapping("/bookings/cancel")
    public ResponseEntity<?> getCancelUrl(
            @RequestParam String userId,
            @RequestParam Long bookingId) {
        log.info("功能C: 获取取消预约URL - userId={}, bookingId={}", userId, bookingId);
        
        try {
            // 验证预约是否属于该用户
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "预约不存在: bookingId=" + bookingId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (!booking.getUserId().equals(userId)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "预约不属于该用户");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            String cancelUrl = bookingService.getCancelUrl(bookingId);
            BookingUrlResponse response = new BookingUrlResponse();
            response.setBookingUrl(cancelUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("功能C: 获取取消预约URL失败 - {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}

