package com.house.rental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.house.rental.entity.RentPayment;

import java.util.List;
import java.util.Map;

/**
 * 租金支付记录服务接口
 */
public interface RentPaymentService extends IService<RentPayment> {
    
    /**
     * 创建租金支付记录
     * @param rentPayment 租金支付记录信息
     * @return 创建结果
     */
    boolean createPayment(RentPayment rentPayment);
    
    /**
     * 租金支付确认
     * @param paymentId 支付记录ID
     * @param transactionNo 交易流水号
     * @param paymentMethod 支付方式
     * @param remark 备注
     * @return 确认结果
     */
    boolean confirmPayment(Long paymentId, String transactionNo, String paymentMethod, String remark);
    
    /**
     * 分页查询合同的支付记录
     * @param contractId 合同ID
     * @param page 分页参数
     * @return 支付记录列表
     */
    Page<RentPayment> getContractPaymentList(Long contractId, Page<RentPayment> page);
    
    /**
     * 生成租金账单
     * @param contractId 合同ID
     * @return 生成结果
     */
    boolean generateRentBill(Long contractId);
    
    /**
     * 获取支付记录详情
     * @param paymentId 支付记录ID
     * @return 支付记录详情
     */
    Map<String, Object> getPaymentDetail(Long paymentId);
} 