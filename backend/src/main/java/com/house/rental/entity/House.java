package com.house.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("house")
public class House {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long ownerId;
    
    private String title;
    private String description;
    private String province;
    private String city;
    private String district;
    private String address;
    private Integer roomCount;
    private Integer hallCount;
    private Integer bathroomCount;
    private BigDecimal area;
    private BigDecimal price;
    private BigDecimal deposit;
    private Integer floor;
    private Integer totalFloor;
    private Integer hasElevator;
    private String orientation;
    private String decoration;
    private String facilities;
    private String images;
    private String contactName;
    private String contactPhone;
    private Integer status;
    private Integer deleted;
    
    // 租赁状态：0-未出租，1-已出租
    private Integer leaseStatus;
    
    private Double latitude;  // 纬度
    private Double longitude;  // 经度
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 兼容旧代码，将 status 当作 leaseStatus 使用
    public Integer getLeaseStatus() {
        return leaseStatus != null ? leaseStatus : (status == 2 ? 1 : 0);
    }
    
    public void setLeaseStatus(Integer leaseStatus) {
        this.leaseStatus = leaseStatus;
    }
} 