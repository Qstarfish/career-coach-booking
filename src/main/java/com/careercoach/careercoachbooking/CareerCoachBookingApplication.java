package com.careercoach.careercoachbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.careercoach.careercoachbooking.mapper")
public class CareerCoachBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerCoachBookingApplication.class, args);
    }

}
