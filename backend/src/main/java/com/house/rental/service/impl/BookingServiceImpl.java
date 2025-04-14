package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.common.exception.BusinessException;
import com.house.rental.entity.Booking;
import com.house.rental.entity.House;
import com.house.rental.entity.Message;
import com.house.rental.mapper.BookingMapper;
import com.house.rental.mapper.HouseMapper;
import com.house.rental.service.BookingService;
import com.house.rental.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    
    @Autowired
    private BookingMapper bookingMapper;
    
    @Autowired
    private HouseMapper houseMapper;
    
    @Autowired
    private MessageService messageService;
    
    @Override
    @Transactional
    public void createBooking(Booking booking) {
        House house = houseMapper.selectById(booking.getHouseId());
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        
        // 验证必填字段
        if (booking.getBookingTime() == null) {
            throw new BusinessException("请选择预约时间");
        }
        if (booking.getContactName() == null || booking.getContactName().trim().isEmpty()) {
            throw new BusinessException("请填写联系人姓名");
        }
        if (booking.getContactPhone() == null || booking.getContactPhone().trim().isEmpty()) {
            throw new BusinessException("请填写联系电话");
        }
        
        booking.setStatus(0);  // 待确认
        bookingMapper.insert(booking);
        
        // 创建预约消息通知房东
        Message ownerMessage = new Message();
        ownerMessage.setUserId(house.getOwnerId());
        ownerMessage.setType(2);  // 预约消息
        ownerMessage.setTitle("新的预约请求");
        ownerMessage.setContent("您的房源\"" + house.getTitle() + "\"收到了新的预约请求，请及时处理。");
        messageService.createMessage(ownerMessage);
    }
    
    @Override
    public IPage<Booking> getUserBookingList(Long userId, Integer page, Integer size) {
        return bookingMapper.getBookingList(new Page<>(page, size), userId);
    }
    
    @Override
    public IPage<Booking> getAllBookingList(Integer page, Integer size, String username, String houseTitle, Integer status, String startDate, String endDate) {
        return bookingMapper.getAllBookingList(new Page<>(page, size), username, houseTitle, status, startDate, endDate);
    }
    
    @Override
    public IPage<Booking> getLandlordBookingList(Long landlordId, Integer page, Integer size) {
        return bookingMapper.getLandlordBookingList(new Page<>(page, size), landlordId);
    }
    
    @Override
    @Transactional
    public void cancelBooking(Long id, Long userId) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        if (booking.getStatus() != 0 && booking.getStatus() != 1) {
            throw new BusinessException("当前状态不能取消");
        }
        
        booking.setStatus(2);  // 已取消
        bookingMapper.updateById(booking);
        
        // 创建消息通知
        House house = houseMapper.selectById(booking.getHouseId());
        Message ownerMessage = new Message();
        ownerMessage.setUserId(house.getOwnerId());
        ownerMessage.setType(2);  // 预约消息
        ownerMessage.setTitle("预约已取消");
        ownerMessage.setContent("用户已取消房源\"" + house.getTitle() + "\"的预约。");
        messageService.createMessage(ownerMessage);
    }
    
    @Override
    @Transactional
    public void confirmBooking(Long id, Long landlordId) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        House house = houseMapper.selectById(booking.getHouseId());
        if (house == null || !house.getOwnerId().equals(landlordId)) {
            throw new BusinessException("无权操作");
        }
        if (booking.getStatus() != 0) {
            throw new BusinessException("当前状态不能确认");
        }
        
        booking.setStatus(1);  // 已确认
        bookingMapper.updateById(booking);
        
        // 创建消息通知
        Message ownerMessage = new Message();
        ownerMessage.setUserId(house.getOwnerId());
        ownerMessage.setType(2);  // 预约消息
        ownerMessage.setTitle("预约已确认");
        ownerMessage.setContent("房东已确认房源\"" + house.getTitle() + "\"的预约。");
        messageService.createMessage(ownerMessage);
    }
    
    @Override
    @Transactional
    public void completeBooking(Long id, Long landlordId) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        House house = houseMapper.selectById(booking.getHouseId());
        if (house == null || !house.getOwnerId().equals(landlordId)) {
            throw new BusinessException("无权操作");
        }
        if (booking.getStatus() != 1) {
            throw new BusinessException("当前状态不能完成");
        }
        
        booking.setStatus(3);  // 已完成
        bookingMapper.updateById(booking);
        
        // 创建消息通知
        Message ownerMessage = new Message();
        ownerMessage.setUserId(house.getOwnerId());
        ownerMessage.setType(2);  // 预约消息
        ownerMessage.setTitle("预约已完成");
        ownerMessage.setContent("房东已确认房源\"" + house.getTitle() + "\"的预约已完成。");
        messageService.createMessage(ownerMessage);
    }
    
    @Override
    @Transactional
    public void deleteBooking(Long id, Long userId) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查是否是预约人或房东
        if (booking.getUserId().equals(userId)) {
            bookingMapper.deleteById(id);
            return;
        }
        
        House house = houseMapper.selectById(booking.getHouseId());
        if (house != null && house.getOwnerId().equals(userId)) {
            bookingMapper.deleteById(id);
            return;
        }
        
        throw new BusinessException("无权操作");
    }
    
    @Override
    @Transactional
    public void batchConfirmBooking(List<Long> ids, Long landlordId) {
        for (Long id : ids) {
            try {
                confirmBooking(id, landlordId);
            } catch (Exception e) {
                log.warn("批量确认预约失败，ID: {}, 原因: {}", id, e.getMessage());
            }
        }
    }
    
    @Override
    @Transactional
    public void batchCancelBooking(List<Long> ids, Long landlordId) {
        for (Long id : ids) {
            Booking booking = bookingMapper.selectById(id);
            if (booking == null) {
                continue;
            }
            House house = houseMapper.selectById(booking.getHouseId());
            if (house == null || !house.getOwnerId().equals(landlordId)) {
                continue;
            }
            if (booking.getStatus() != 0 && booking.getStatus() != 1) {
                continue;
            }
            
            booking.setStatus(2);  // 已取消
            bookingMapper.updateById(booking);
            
            // 创建消息通知
            Message ownerMessage = new Message();
            ownerMessage.setUserId(house.getOwnerId());
            ownerMessage.setType(2);  // 预约消息
            ownerMessage.setTitle("预约已取消");
            ownerMessage.setContent("用户已取消房源\"" + house.getTitle() + "\"的预约。");
            messageService.createMessage(ownerMessage);
        }
    }
    
    @Override
    @Transactional
    public void batchDeleteBooking(List<Long> ids, Long landlordId) {
        for (Long id : ids) {
            Booking booking = bookingMapper.selectById(id);
            if (booking == null) {
                continue;
            }
            House house = houseMapper.selectById(booking.getHouseId());
            if (house == null || !house.getOwnerId().equals(landlordId)) {
                continue;
            }
            bookingMapper.deleteById(id);
        }
    }
    
    @Override
    @Transactional
    public void updateBookingStatus(Long id, Integer status) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        House house = houseMapper.selectById(booking.getHouseId());
        if (house == null) {
            throw new BusinessException("房源不存在");
        }

        // 更新预约状态
        Booking updateBooking = new Booking();
        updateBooking.setId(id);
        updateBooking.setStatus(status);
        bookingMapper.updateById(updateBooking);

        // 创建消息通知
        String statusText = getStatusText(status);
        
        // 通知预约用户
        Message userMessage = new Message();
        userMessage.setUserId(booking.getUserId());
        userMessage.setType(2);  // 预约消息
        userMessage.setTitle("预约状态更新");
        userMessage.setContent("您预约的房源\"" + house.getTitle() + "\"状态已更新为：" + statusText);
        messageService.createMessage(userMessage);

        // 如果是取消预约，通知房东
        if (status == 2) {
            Message ownerMessage = new Message();
            ownerMessage.setUserId(house.getOwnerId());
            ownerMessage.setType(2);  // 预约消息
            ownerMessage.setTitle("预约已取消");
            ownerMessage.setContent("用户已取消房源\"" + house.getTitle() + "\"的预约。");
            messageService.createMessage(ownerMessage);
        }
    }
    
    @Override
    @Transactional
    public void rejectBooking(Long id, Long landlordId) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        House house = houseMapper.selectById(booking.getHouseId());
        if (house == null || !house.getOwnerId().equals(landlordId)) {
            throw new BusinessException("无权操作");
        }
        if (booking.getStatus() != 0) {
            throw new BusinessException("当前状态不能拒绝");
        }
        
        booking.setStatus(4);  // 已拒绝
        bookingMapper.updateById(booking);
        
        // 创建消息通知
        Message userMessage = new Message();
        userMessage.setUserId(booking.getUserId());
        userMessage.setType(2);  // 预约消息
        userMessage.setTitle("预约已被拒绝");
        userMessage.setContent("房东已拒绝您对房源\"" + house.getTitle() + "\"的预约。");
        messageService.createMessage(userMessage);
    }
    
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已取消";
            case 3: return "已完成";
            case 4: return "已拒绝";
            default: return "未知状态";
        }
    }
} 