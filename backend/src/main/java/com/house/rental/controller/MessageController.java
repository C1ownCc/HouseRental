package com.house.rental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.common.Result;
import com.house.rental.entity.Message;
import com.house.rental.service.MessageService;
import com.house.rental.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getMessageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getCurrentUserId();
        Page<Message> messagePage = messageService.getUserMessages(userId, page, size);
        Long unreadCount = messageService.getUnreadCount(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", messagePage.getRecords());
        result.put("total", messagePage.getTotal());
        result.put("unreadCount", unreadCount);
        
        return Result.success(result);
    }
    
    @PutMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id, UserContext.getCurrentUserId());
        return Result.success();
    }
    
    @PutMapping("/read/all")
    public Result<?> markAllAsRead() {
        messageService.markAllAsRead(UserContext.getCurrentUserId());
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<?> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id, UserContext.getCurrentUserId());
        return Result.success();
    }
} 