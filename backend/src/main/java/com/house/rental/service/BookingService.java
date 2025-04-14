package com.house.rental.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.house.rental.entity.Booking;

import java.util.List;

public interface BookingService {
    // 创建预约
    void createBooking(Booking booking);
    
    // 获取用户的预约列表
    IPage<Booking> getUserBookingList(Long userId, Integer page, Integer size);
    
    // 获取所有预约列表（管理员用）
    IPage<Booking> getAllBookingList(Integer page, Integer size, String username, String houseTitle, Integer status, String startDate, String endDate);
    
    // 获取房东的预约列表
    IPage<Booking> getLandlordBookingList(Long landlordId, Integer page, Integer size);
    
    // 取消预约
    void cancelBooking(Long id, Long userId);
    
    // 拒绝预约
    void rejectBooking(Long id, Long landlordId);
    
    // 确认预约
    void confirmBooking(Long id, Long landlordId);
    
    // 完成预约
    void completeBooking(Long id, Long landlordId);
    
    // 删除预约
    void deleteBooking(Long id, Long userId);
    
    // 批量确认预约
    void batchConfirmBooking(List<Long> ids, Long landlordId);
    
    // 批量取消预约
    void batchCancelBooking(List<Long> ids, Long landlordId);
    
    // 批量删除预约
    void batchDeleteBooking(List<Long> ids, Long landlordId);
    
    void updateBookingStatus(Long id, Integer status);
} 