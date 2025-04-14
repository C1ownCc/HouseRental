package com.house.rental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁合同实体类
 */
@Data
@TableName("lease_contract")
public class LeaseContract {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long houseId;
    
    private Long tenantId;
    
    private Long ownerId;
    
    private String contractNumber;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal monthlyRent;
    
    private BigDecimal deposit;
    
    private String paymentMethod;
    
    private String contractFile;
    
    private LocalDateTime signDate;
    
    private Integer tenantSign;
    
    private Integer ownerSign;
    
    private Integer status;
    
    private String remark;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
    
    private Integer deleted;
} 