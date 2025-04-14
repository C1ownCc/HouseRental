package com.house.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("booking")
public class Booking {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long houseId;
    
    @TableField("booking_time")
    private LocalDateTime bookingTime;
    
    private String remark;
    
    private Integer status;  // 状态：0-待确认，1-已确认，2-已取消，3-已完成
    
    private String contactName;
    
    private String contactPhone;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String houseTitle;
    
    @TableField(exist = false)
    private String houseImages;

    @TableField(exist = false)
    private BigDecimal housePrice;
} 