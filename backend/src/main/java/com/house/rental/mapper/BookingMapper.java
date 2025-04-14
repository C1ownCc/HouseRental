package com.house.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.house.rental.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookingMapper extends BaseMapper<Booking> {
    // 获取预约详情（包含用户名、房东名和房源标题）
    IPage<Booking> getBookingList(IPage<Booking> page, @Param("userId") Long userId);
    
    // 房东获取预约列表
    IPage<Booking> getLandlordBookingList(IPage<Booking> page, @Param("landlordId") Long landlordId);

    // 获取所有预约列表（管理员用）
    IPage<Booking> getAllBookingList(IPage<Booking> page, 
        @Param("username") String username,
        @Param("houseTitle") String houseTitle,
        @Param("status") Integer status,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);
} 