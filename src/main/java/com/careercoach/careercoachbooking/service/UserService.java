package com.careercoach.careercoachbooking.service;

import com.careercoach.careercoachbooking.entity.User;
import com.careercoach.careercoachbooking.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 */
@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据userId获取用户，如果不存在则创建
     */
    public User getOrCreateUser(String userId, String name, String email) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            user = new User();
            user.setUserId(userId);
            user.setName(name);
            user.setEmail(email);
            userMapper.insert(user);
            log.info("创建新用户: userId={}, name={}, email={}", userId, name, email);
        } else {
            // 更新用户信息（如果提供了新的信息）
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
            }
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
            }
            userMapper.update(user);
        }
        return user;
    }
    
    /**
     * 根据userId获取用户
     */
    public User getUserByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }
    
    /**
     * 根据邮箱获取用户
     */
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}

