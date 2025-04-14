package com.house.rental;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.house.rental.mapper")
public class HouseRentalApplication {
    public static void main(String[] args) {
        SpringApplication.run(HouseRentalApplication.class, args);
    }
} 