package com.careercoach.careercoachbooking.service;

import com.careercoach.careercoachbooking.config.AppConfig;
import com.careercoach.careercoachbooking.dto.CalCreateBookingRequest;
import com.careercoach.careercoachbooking.dto.CalCreateBookingResponse;
import com.careercoach.careercoachbooking.dto.CalCancelBookingRequest;
import com.careercoach.careercoachbooking.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cal.com service class
 */
@Slf4j
@Service
public class CalService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取用户信息
     * GET /v2/me
     */
    public Map<String, Object> getUserInfo() {
        try {
            String apiUrl = appConfig.getCal().getApi().getUrl() + "/me";
            HttpHeaders headers = createAuthHeaders();

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if ("success".equals(responseBody.get("status"))) {
                    return (Map<String, Object>) responseBody.get("data");
                }
            }

            log.error("获取用户信息失败: status={}", response.getStatusCode());
            throw new RuntimeException("获取用户信息失败");
        } catch (RestClientException e) {
            log.error("调用Cal.com API获取用户信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用Cal.com API失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取事件类型列表
     * GET /v2/event-types
     */
    public List<Map<String, Object>> getEventTypes() {
        try {
            String apiUrl = appConfig.getCal().getApi().getUrl() + "/v2/event-types";
            HttpHeaders headers = createAuthHeaders();

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if ("success".equals(responseBody.get("status"))) {
                    return (List<Map<String, Object>>) responseBody.get("data");
                }
            }

            log.error("获取事件类型失败: status={}", response.getStatusCode());
            throw new RuntimeException("获取事件类型失败");
        } catch (RestClientException e) {
            log.error("调用Cal.com API获取事件类型失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用Cal.com API失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成预约页面URL
     * 格式: https://cal.com/{username}/{eventTypeSlug}
     */
    public String generateBookingPageUrl() {
        try {
            // 从配置获取用户名
            String username = appConfig.getBooking().getUsername();
            
            // 获取事件类型列表
            List<Map<String, Object>> eventTypes = getEventTypes();
            if (eventTypes == null || eventTypes.isEmpty()) {
                throw new RuntimeException("没有可用的事件类型");
            }
            
            // 优先使用配置中指定的eventTypeSlug，如果没有配置则使用eventTypes中第一个的meeting
            String eventTypeSlug = appConfig.getCal().getEventTypeSlug();
            if (eventTypeSlug == null || eventTypeSlug.isEmpty()) {
                eventTypeSlug = (String) eventTypes.get(0).get("slug");
            }
            
            String bookingUrl = String.format("https://cal.com/%s/%s", username, eventTypeSlug);
            log.info("生成预约页面URL: {}", bookingUrl);
            return bookingUrl;
        } catch (Exception e) {
            log.error("生成预约页面URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成预约页面URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据bookingUid生成取消预约的URL
     * 格式: https://cal.com/booking/{bookingUid}?cancel=true
     */
    public String generateCancelUrl(String bookingUid) {
        if (bookingUid == null || bookingUid.trim().isEmpty()) {
            throw new RuntimeException("bookingUid不能为空");
        }
        try {
            String trimmed = bookingUid.trim();
            String encoded = java.net.URLEncoder.encode(trimmed, java.nio.charset.StandardCharsets.UTF_8);
            String base = "https://cal.com";
            String url = String.format("%s/booking/%s?cancel=true", base, encoded);
            log.info("生成取消预约URL: {}", url);
            return url;
        } catch (Exception e) {
            log.error("生成取消预约URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成取消预约URL失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 通过cal.com API获取booking的uid
     * 参考: https://cal.com/docs/api-reference/v2/bookings/get-a-booking
     */
    public String getBookingUid(Long calBookingId) {
        try {
            String apiUrl = appConfig.getCal().getApi().getUrl() + "/bookings/" + calBookingId;
            HttpHeaders headers = createAuthHeaders();
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.GET, 
                entity, 
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> booking = response.getBody();
                // Cal.com API返回的格式可能是 { "booking": { "uid": "..." } } 或直接 { "uid": "..." }
                Object bookingObj = booking.get("booking");
                if (bookingObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> bookingMap = (Map<String, Object>) bookingObj;
                    Object uid = bookingMap.get("uid");
                    if (uid != null) {
                        return uid.toString();
                    }
                }
                // 尝试直接从根对象获取uid
                Object uid = booking.get("uid");
                if (uid != null) {
                    return uid.toString();
                }
                log.warn("无法从Cal.com API响应中获取uid: calBookingId={}", calBookingId);
                return null;
            } else {
                log.error("获取Cal.com预约详情失败: status={}, calBookingId={}", 
                    response.getStatusCode(), calBookingId);
                return null;
            }
        } catch (RestClientException e) {
            log.error("调用Cal.com API获取预约详情失败: calBookingId={}, error={}", 
                calBookingId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 创建认证请求头
     * 根据Cal.com API文档，需要设置 cal-api-version header
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + appConfig.getCal().getApi().getKey());
        headers.set("cal-api-version", "2024-06-14"); // 使用Event Types API要求的版本号
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
