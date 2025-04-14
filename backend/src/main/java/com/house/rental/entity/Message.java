package com.house.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private Integer type;  // 0: 系统消息, 1: 预约消息, 2: 收藏消息
    
    private Integer status;  // 0: 未读, 1: 已读
    
    // 阅读状态：0-未读，1-已读 (兼容数据库中的read_status字段)
    @TableField("read_status")
    private Integer readStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
    
    // 兼容旧代码，使用status作为readStatus
    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
        this.status = readStatus;  // 同时设置status，保持一致性
    }
} 