package com.careercoach.careercoachbooking.mapper;

import com.careercoach.careercoachbooking.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预约Mapper接口
 */
@Mapper
public interface BookingMapper {
    int insert(Booking booking);
    int update(Booking booking);
    Booking findByCalBookingId(@Param("calBookingId") Long calBookingId);
    List<Booking> findByUserId(@Param("userId") String userId);

}

