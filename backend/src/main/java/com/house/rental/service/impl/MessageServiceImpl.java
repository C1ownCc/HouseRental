package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.rental.entity.Message;
import com.house.rental.mapper.MessageMapper;
import com.house.rental.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Page<Message> getUserMessages(Long userId, Integer page, Integer size) {
        Page<Message> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreatedTime);
        return page(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        Message message = new Message();
        message.setId(messageId);
        message.setStatus(1);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getId, messageId)
                .eq(Message::getUserId, userId);
        update(message, wrapper);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        Message message = new Message();
        message.setStatus(1);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
                .eq(Message::getStatus, 0);
        update(message, wrapper);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getId, messageId)
                .eq(Message::getUserId, userId);
        remove(wrapper);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
                .eq(Message::getStatus, 0);
        return count(wrapper);
    }

    @Override
    @Transactional
    public void createMessage(Message message) {
        message.setStatus(0);
        save(message);
    }
} 