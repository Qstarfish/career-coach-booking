package com.careercoach.careercoachbooking.service;

import com.careercoach.careercoachbooking.config.AppConfig;
import com.careercoach.careercoachbooking.dto.BookingResponse;
import com.careercoach.careercoachbooking.entity.Booking;
import com.careercoach.careercoachbooking.entity.BookingStatus;
import com.careercoach.careercoachbooking.entity.User;
import com.careercoach.careercoachbooking.mapper.BookingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约服务类
 */
@Slf4j
@Service
public class BookingService {
    
    @Autowired
    private BookingMapper bookingMapper;
    
    @Autowired
    private CalService calService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AppConfig appConfig;

    /**
     * 功能A: 获取预约页面URL
     * 1. 查询user表获取用户信息（如果不存在则创建）
     * 2. 通过Cal.com API获取用户信息和事件类型
     * 3. 生成真实可预约的URL
     */
    public String createBookingAndGetUrl(String userId) {
        log.info("功能A: 获取预约URL - userId={}", userId);
        
        // 1. 查询user表获取用户信息，如果不存在则创建默认用户
        User user = userService.getUserByUserId(userId);
        if (user == null) {
            user = userService.getOrCreateUser(userId, "Default User", "user@example.com");
        }
        // 2. 通过CalService生成预约页面URL
        String bookingUrl = calService.generateBookingPageUrl();
        
        log.info("功能A: 成功生成预约URL - userId={}, bookingUrl={}", userId, bookingUrl);
        
        return bookingUrl;
    }
    
    /**
     * 功能B: 获取用户的预约列表
     * 查询数据库中已落库的预约记录
     */
    public List<BookingResponse> getUserBookings(String userId) {
        log.info("功能B: 查询预约列表 - userId={}", userId);
        
        List<Booking> bookings = bookingMapper.findByUserId(userId);
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    /**
     * 功能C: 获取取消预约的URL
     * 1. 查询数据库中的预约记录
     * 2. 通过cal.com API获取booking的uid
     * 3. 生成真实可取消预约的URL
     * 
     * 注意：返回URL后，用户访问该URL取消预约，Cal.com会通过webhook推送BOOKING_CANCELLED事件更新数据库
     */
    public String getCancelUrl(Long bookingId) {
        log.info("功能C: 获取取消预约URL - bookingId={}", bookingId);
        
        // 1.查询数据库中的预约记录
        Booking booking = bookingMapper.findByCalBookingId(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约不存在: bookingId=" + bookingId);
        }
        if (booking.getCalBookingId() == null) {
            throw new RuntimeException("预约未关联Cal.com预约ID: bookingId=" + bookingId);
        }
        // 2. 从数据库记录中获取bookingUid
        String bookingUid = booking.getBookingUid();

        // 生成真实可取消预约的URL
        String cancelUrl = calService.generateCancelUrl(bookingUid);
        
        log.info("功能C: 成功获取取消预约URL - bookingId={}, calBookingId={}, cancelUrl={}", 
            bookingId, booking.getCalBookingId(), cancelUrl);
        
        return cancelUrl;
    }
    
    /**
     * 功能D: 处理Cal.com Webhook事件 - BOOKING_CREATED/BOOKING_CANCELLED/MEETING_ENDED
     * 接收webhook推送的BOOKING_CREATED事件并落库
     */
    @Transactional
    public void handleWebhookEvent(String triggerEvent, Long calBookingId, 
                                   String userEmail, String userName,
                                   String coachName, String coachEmail,
                                   String startTime, String endTime,
                                   Long eventTypeId, String status,
                                    String bookingUid) {
        log.info("功能D: 处理Webhook事件 - triggerEvent={}, calBookingId={},bookingUid={}", triggerEvent, calBookingId,bookingUid);
        
        if ("BOOKING_CREATED".equals(triggerEvent)) {
            handleBookingCreated(calBookingId, userEmail, userName, coachName, coachEmail, 
                startTime, endTime, eventTypeId, status, bookingUid);
        } else if ("BOOKING_CANCELLED".equals(triggerEvent)) {
            handleBookingCancelled(calBookingId);
        } else if ("MEETING_ENDED".equals(triggerEvent)) {
            handleMeetingEnded(calBookingId);
        } else {
            log.warn("未处理的Webhook事件类型: triggerEvent={}", triggerEvent);
        }
    }

    /**
     * 处理BOOKING_CREATED事件 - 落库
     */
    private void handleBookingCreated(Long calBookingId, String userEmail, String userName,
                                     String coachName, String coachEmail,
                                     String startTime, String endTime,
                                     Long eventTypeId, String status, String bookingUid) {
        log.info("功能D: 处理BOOKING_CREATED事件 - calBookingId={}", calBookingId);
        
        // 检查是否已存在
        Booking existingBooking = bookingMapper.findByCalBookingId(calBookingId);
        if (existingBooking != null) {
            log.info("预约已存在，更新记录 - calBookingId={}", calBookingId);
            // 更新现有记录
            existingBooking.setCoachName(coachName);
            existingBooking.setCoachEmail(coachEmail);
            existingBooking.setStartTime(parseDateTime(startTime));
            existingBooking.setEndTime(parseDateTime(endTime));
            existingBooking.setStatus(BookingStatus.BOOKING_CREATED.getCode());
            existingBooking.setCalEventTypeId(eventTypeId);
            bookingMapper.update(existingBooking);
            return;
        }
        
        // 通过邮箱查找或创建用户
        String userId = null;
        if (userEmail != null && !userEmail.isEmpty()) {
            User user = userService.getUserByEmail(userEmail);
            if (user != null) {
                userId = user.getUserId();
            } else {
                // 如果用户不存在，创建新用户
                userId = "user_" + System.currentTimeMillis();
                userService.getOrCreateUser(userId, userName != null ? userName : "Unknown", userEmail);
            }
        }
        
        // 创建新预约记录并落库
        Booking booking = new Booking();
        booking.setCalBookingId(calBookingId);
        booking.setUserId(userId);
        booking.setCoachName(coachName);
        booking.setCoachEmail(coachEmail);
        booking.setStartTime(parseDateTime(startTime));
        booking.setEndTime(parseDateTime(endTime));
        booking.setStatus(BookingStatus.BOOKING_CREATED.getCode());
        booking.setCalEventTypeId(eventTypeId);
        booking.setBookingUid(bookingUid);
        bookingMapper.insert(booking);
        
        log.info("功能D: 成功落库 - calBookingId={}, userId={}, bookingId={}", 
            calBookingId, userId, booking.getId());
    }
    
    /**
     * 处理BOOKING_CANCELLED事件 - 更新数据库状态
     */
    private void handleBookingCancelled(Long calBookingId) {
        log.info("功能C: 处理BOOKING_CANCELLED事件 - calBookingId={}", calBookingId);
        
        Booking booking = bookingMapper.findByCalBookingId(calBookingId);
        if (booking != null) {
            booking.setStatus(BookingStatus.BOOKING_CANCELLED.getCode());
            bookingMapper.update(booking);
            log.info("功能C: 成功更新数据库状态为已取消 - calBookingId={}, bookingId={}", 
                calBookingId, booking.getId());
        } else {
            log.warn("未找到对应的预约记录 - calBookingId={}", calBookingId);
        }
    }
    
    /**
     * 处理MEETING_ENDED事件
     */
    private void handleMeetingEnded(Long calBookingId) {
        log.info("处理MEETING_ENDED事件 - calBookingId={}", calBookingId);
        
        Booking booking = bookingMapper.findByCalBookingId(calBookingId);
        if (booking != null) {
            booking.setStatus(BookingStatus.MEETING_ENDED.getCode());
            bookingMapper.update(booking);
            log.info("会议结束 - calBookingId={}, bookingId={}", calBookingId, booking.getId());
        }
    }
    
    /**
     * 转换Booking实体为响应DTO
     */
    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setStatus(booking.getStatus());
        response.setCoachName(booking.getCoachName());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        return response;
    }
    
    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            // Cal.com通常使用ISO 8601格式，可能包含Z后缀
            if (dateTimeStr.endsWith("Z")) {
                return LocalDateTime.parse(dateTimeStr.substring(0, dateTimeStr.length() - 1));
            } else {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
            }
        } catch (Exception e) {
            log.error("解析日期时间失败: {}", dateTimeStr, e);
            return null;
        }
    }

    public Booking getBookingById(Long bookingId) {
        return bookingMapper.findByCalBookingId(bookingId);
    }
}
