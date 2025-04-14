package com.house.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long houseId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String houseTitle;
    
    @TableField(exist = false)
    private String houseCoverImage;
    
    @TableField(exist = false)
    private String houseType;
    
    @TableField(exist = false)
    private String houseArea;
    
    @TableField(exist = false)
    private BigDecimal housePrice;
} 