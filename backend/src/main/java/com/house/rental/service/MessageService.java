package com.house.rental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.house.rental.entity.Message;

public interface MessageService extends IService<Message> {
    // 获取用户消息列表
    Page<Message> getUserMessages(Long userId, Integer page, Integer size);
    
    // 标记消息为已读
    void markAsRead(Long messageId, Long userId);
    
    // 标记所有消息为已读
    void markAllAsRead(Long userId);
    
    // 删除消息
    void deleteMessage(Long messageId, Long userId);
    
    // 获取未读消息数量
    Long getUnreadCount(Long userId);
    
    // 创建新消息
    void createMessage(Message message);
} 