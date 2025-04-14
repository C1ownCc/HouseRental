package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.rental.entity.LeaseContract;
import com.house.rental.entity.Message;
import com.house.rental.entity.RentPayment;
import com.house.rental.mapper.RentPaymentMapper;
import com.house.rental.service.LeaseContractService;
import com.house.rental.service.MessageService;
import com.house.rental.service.RentPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租金支付记录服务实现类
 */
@Service
@Slf4j
public class RentPaymentServiceImpl extends ServiceImpl<RentPaymentMapper, RentPayment> implements RentPaymentService {

    @Autowired
    private LeaseContractService leaseContractService;
    
    @Autowired
    private MessageService messageService;

    @Override
    @Transactional
    public boolean createPayment(RentPayment rentPayment) {
        // 设置支付状态为未支付
        rentPayment.setPaymentStatus(0);
        
        // 保存支付记录
        boolean result = save(rentPayment);
        
        if (result) {
            // 发送消息给租户
            LeaseContract contract = leaseContractService.getById(rentPayment.getContractId());
            if (contract != null) {
                sendPaymentMessage(contract.getTenantId(), 
                        "租金待支付通知", 
                        "您有一笔租金需要支付，租期：" + 
                                rentPayment.getPeriodStart() + " 至 " + 
                                rentPayment.getPeriodEnd() + "，金额：" + 
                                rentPayment.getAmount() + "元。", 
                        4);
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean confirmPayment(Long paymentId, String transactionNo, String paymentMethod, String remark) {
        RentPayment payment = getById(paymentId);
        if (payment == null) {
            return false;
        }
        
        // 更新支付状态为已支付
        payment.setPaymentStatus(1);
        payment.setTransactionNo(transactionNo);
        
        // 明确设置支付日期为当前日期
        payment.setPaymentDate(LocalDate.now());
        
        // 设置支付方式和备注
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            payment.setPaymentMethod(paymentMethod);
        }
        
        if (remark != null) {
            payment.setRemark(remark);
        }
        
        boolean result = updateById(payment);
        
        if (result) {
            // 发送消息给租户和房东
            LeaseContract contract = leaseContractService.getById(payment.getContractId());
            if (contract != null) {
                // 发送消息给租户
                sendPaymentMessage(contract.getTenantId(), 
                        "租金支付成功", 
                        "您的租金支付已成功，租期：" + 
                                payment.getPeriodStart() + " 至 " + 
                                payment.getPeriodEnd() + "，金额：" + 
                                payment.getAmount() + "元。", 
                        4);
                
                // 发送消息给房东
                sendPaymentMessage(contract.getOwnerId(), 
                        "租金收款通知", 
                        "您已收到一笔租金，租期：" + 
                                payment.getPeriodStart() + " 至 " + 
                                payment.getPeriodEnd() + "，金额：" + 
                                payment.getAmount() + "元。", 
                        4);
            }
        }
        
        return result;
    }

    @Override
    public Page<RentPayment> getContractPaymentList(Long contractId, Page<RentPayment> page) {
        // 构建查询条件
        LambdaQueryWrapper<RentPayment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RentPayment::getContractId, contractId)
                .eq(RentPayment::getDeleted, 0)
                .orderByDesc(RentPayment::getPeriodStart);
        
        // 分页查询
        return page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean generateRentBill(Long contractId) {
        LeaseContract contract = leaseContractService.getById(contractId);
        if (contract == null || contract.getStatus() != 3) {
            // 只有已生效的合同才能生成账单
            return false;
        }
        
        // 获取合同的租期
        LocalDate startDate = contract.getStartDate();
        LocalDate endDate = contract.getEndDate();
        
        // 获取已生成的账单
        LambdaQueryWrapper<RentPayment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RentPayment::getContractId, contractId)
                .eq(RentPayment::getDeleted, 0);
        List<RentPayment> existingPayments = list(queryWrapper);
        
        // 根据支付方式生成账单
        List<RentPayment> newPayments = new ArrayList<>();
        String paymentMethod = contract.getPaymentMethod();
        
        // 计算需要生成的账单周期
        int intervalMonths;
        switch (paymentMethod) {
            case "月付":
                intervalMonths = 1;
                break;
            case "季付":
                intervalMonths = 3;
                break;
            case "半年付":
                intervalMonths = 6;
                break;
            case "年付":
                intervalMonths = 12;
                break;
            default:
                intervalMonths = 1;
        }
        
        // 生成账单
        LocalDate currentDate = startDate;
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            // 计算账单周期结束日期
            LocalDate periodEndDate = currentDate.plusMonths(intervalMonths).minusDays(1);
            if (periodEndDate.isAfter(endDate)) {
                periodEndDate = endDate;
            }
            
            // 检查该周期是否已有账单
            final LocalDate finalCurrentDate = currentDate;
            final LocalDate finalPeriodEndDate = periodEndDate;
            boolean exists = existingPayments.stream().anyMatch(payment -> 
                    (payment.getPeriodStart().isEqual(finalCurrentDate) || payment.getPeriodStart().isAfter(finalCurrentDate)) &&
                    (payment.getPeriodEnd().isEqual(finalPeriodEndDate) || payment.getPeriodEnd().isBefore(finalPeriodEndDate))
            );
            
            if (!exists) {
                // 创建新账单
                RentPayment newPayment = new RentPayment();
                newPayment.setContractId(contractId);
                newPayment.setPeriodStart(currentDate);
                newPayment.setPeriodEnd(periodEndDate);
                
                // 设置payment_date为null，避免SQL插入错误
                newPayment.setPaymentDate(null);
                
                // 计算租金
                long days = ChronoUnit.DAYS.between(currentDate, periodEndDate) + 1;
                long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                double monthlyRent = contract.getMonthlyRent().doubleValue();
                double amount = (monthlyRent * intervalMonths) * ((double) days / (30.0 * intervalMonths));
                
                if (periodEndDate.isEqual(endDate) && !currentDate.isEqual(startDate)) {
                    // 最后一期，按天计算
                    amount = (monthlyRent / 30) * days;
                }
                
                newPayment.setAmount(new java.math.BigDecimal(amount).setScale(2, java.math.RoundingMode.HALF_UP));
                newPayment.setPaymentStatus(0);
                
                newPayments.add(newPayment);
            }
            
            // 移到下一个周期
            currentDate = periodEndDate.plusDays(1);
        }
        
        // 批量保存新生成的账单
        if (!newPayments.isEmpty()) {
            boolean result = false;
            try {
                // 使用自定义的批量插入方法，确保payment_date字段为NULL
                int insertCount = baseMapper.batchInsertWithNullPaymentDate(newPayments);
                result = insertCount > 0;
                
                if (result) {
                    // 发送消息给租户
                    sendPaymentMessage(contract.getTenantId(), 
                            "租金账单已生成", 
                            "您的租赁合同已生成新的租金账单，请查看并及时支付。", 
                            4);
                }
            } catch (Exception e) {
                // 记录异常信息
                log.error("生成租金账单异常:", e);
                throw e;
            }
            
            return result;
        }
        
        return true;
    }

    @Override
    public Map<String, Object> getPaymentDetail(Long paymentId) {
        RentPayment payment = getById(paymentId);
        if (payment == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("payment", payment);
        
        // 添加合同信息
        LeaseContract contract = leaseContractService.getById(payment.getContractId());
        if (contract != null) {
            result.put("contract", contract);
        }
        
        return result;
    }
    
    /**
     * 发送支付相关消息
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型
     */
    private void sendPaymentMessage(Long userId, String title, String content, Integer type) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setStatus(0);
        message.setReadStatus(0);
        messageService.save(message);
    }
} 