package com.house.rental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租金支付记录实体类
 */
@Data
@TableName("rent_payment")
public class RentPayment {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long contractId;
    
    private BigDecimal amount;
    
    private LocalDate paymentDate;
    
    private String paymentMethod;
    
    private Integer paymentStatus;
    
    private String transactionNo;
    
    private LocalDate periodStart;
    
    private LocalDate periodEnd;
    
    private String remark;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
    
    private Integer deleted;
} 