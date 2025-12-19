package com.careercoach.careercoachbooking.controller;

import com.careercoach.careercoachbooking.dto.CalWebhookRequest;
import com.careercoach.careercoachbooking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Webhook控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/webhook")
public class WebhookController {
    
    @Autowired
    private BookingService bookingService;
    
    /**
     * 功能D: 接收Cal.com的Webhook事件
     * POST /api/webhook/cal
     */
    @PostMapping("/cal")
    public ResponseEntity<Map<String, String>> handleCalWebhook(@RequestBody CalWebhookRequest webhookRequest) {
        log.info("收到Cal.com Webhook事件: triggerEvent={}", webhookRequest.getTriggerEvent());
        
        try {
            CalWebhookRequest.Payload payload = webhookRequest.getPayload();
            if (payload == null) {
                log.warn("Webhook payload为空");
                return ResponseEntity.badRequest().body(createErrorResponse("payload为空"));
            }
            
            // 提取信息
            String triggerEvent = webhookRequest.getTriggerEvent();
            Long calBookingId = payload.getBookingId();
            String startTime = payload.getStartTime();
            String endTime = payload.getEndTime();
            String status = payload.getStatus();
            String calBookingUid = payload.getUid();
            
            // 提取组织者信息
            String coachName = null;
            String coachEmail = null;
            if (payload.getOrganizer() != null) {
                coachName = payload.getOrganizer().getName();
                coachEmail = payload.getOrganizer().getEmail();
            }
            
            // 提取参与者信息
            String userName = null;
            String userEmail = null;
            if (payload.getAttendees() != null && !payload.getAttendees().isEmpty()) {
                CalWebhookRequest.Attendee firstAttendee = payload.getAttendees().get(0);
                userName = firstAttendee.getName();
                userEmail = firstAttendee.getEmail();
            }
            
            log.info("处理Webhook事件详情: triggerEvent={}, calBookingId={}, uid={}, status={},calBookingUid={}",
                triggerEvent, calBookingId, payload.getUid(), status,calBookingUid);
            
            // 处理Webhook事件
            bookingService.handleWebhookEvent(
                triggerEvent,
                calBookingId,
                userEmail,
                userName,
                coachName,
                coachEmail,
                startTime,
                endTime,
                null,
                status,
                calBookingUid
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Webhook处理成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("处理Webhook事件失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }
}

