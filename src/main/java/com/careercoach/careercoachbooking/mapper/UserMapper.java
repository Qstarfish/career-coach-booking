package com.careercoach.careercoachbooking.mapper;

import com.careercoach.careercoachbooking.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    User findByUserId(@Param("userId") String userId);
    User findByEmail(@Param("email") String email);
    int insert(User user);
    int update(User user);
}

