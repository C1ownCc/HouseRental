package com.house.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.house.rental.entity.RentPayment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租金支付记录Mapper接口
 */
public interface RentPaymentMapper extends BaseMapper<RentPayment> {
    
    /**
     * 批量插入租金支付记录，明确设置payment_date为特殊日期值
     */
    @Insert("<script>" +
            "INSERT INTO rent_payment (contract_id, amount, payment_date, payment_method, payment_status, period_start, period_end) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.contractId}, #{item.amount}, '1970-01-01', '未支付', #{item.paymentStatus}, #{item.periodStart}, #{item.periodEnd})" +
            "</foreach>" +
            "</script>")
    int batchInsertWithNullPaymentDate(@Param("list") List<RentPayment> list);
} 