package com.house.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.common.response.Result;
import com.house.rental.entity.RentPayment;
import com.house.rental.service.RentPaymentService;
import com.house.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租金支付控制器
 */
@RestController
@RequestMapping("/rent-payment")
public class RentPaymentController {

    @Autowired
    private RentPaymentService rentPaymentService;

    @Autowired
    private UserService userService;

    /**
     * 创建租金支付记录
     * @param rentPayment 租金支付记录信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Void> createPayment(@RequestBody RentPayment rentPayment) {
        try {
            boolean success = rentPaymentService.createPayment(rentPayment);
            if (success) {
                return Result.success();
            } else {
                return Result.error("创建租金支付记录失败");
            }
        } catch (Exception e) {
            return Result.error("创建租金支付记录异常: " + e.getMessage());
        }
    }

    /**
     * 租金支付确认
     * @param paymentId 支付记录ID
     * @param transactionNo 交易流水号
     * @param payment 支付信息
     * @return 确认结果
     */
    @PutMapping("/confirm/{paymentId}")
    public Result<Void> confirmPayment(
            @PathVariable Long paymentId,
            @RequestParam String transactionNo,
            @RequestBody(required = false) RentPayment payment) {
        try {
            String paymentMethod = payment != null ? payment.getPaymentMethod() : null;
            String remark = payment != null ? payment.getRemark() : null;
            boolean success = rentPaymentService.confirmPayment(paymentId, transactionNo, paymentMethod, remark);
            if (success) {
                return Result.success();
            } else {
                return Result.error("确认支付失败，请检查支付记录状态");
            }
        } catch (Exception e) {
            return Result.error("确认支付异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询合同的支付记录
     * @param contractId 合同ID
     * @param current 当前页
     * @param size 每页大小
     * @return 支付记录列表
     */
    @GetMapping("/contract/{contractId}/page")
    public Result<Page<RentPayment>> getContractPaymentList(
            @PathVariable Long contractId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<RentPayment> page = new Page<>(current, size);
            Page<RentPayment> paymentPage = rentPaymentService.getContractPaymentList(contractId, page);
            return Result.success(paymentPage);
        } catch (Exception e) {
            return Result.error("获取支付记录列表异常: " + e.getMessage());
        }
    }

    /**
     * 获取合同所有支付记录（不分页）
     * @param contractId 合同ID
     * @return 支付记录列表
     */
    @GetMapping("/contract/{contractId}")
    public Result<List<RentPayment>> getAllContractPayments(@PathVariable Long contractId) {
        try {
            LambdaQueryWrapper<RentPayment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RentPayment::getContractId, contractId)
                    .eq(RentPayment::getDeleted, 0)
                    .orderByDesc(RentPayment::getPaymentDate);
            
            List<RentPayment> payments = rentPaymentService.list(queryWrapper);
            return Result.success(payments);
        } catch (Exception e) {
            return Result.error("获取合同支付记录失败: " + e.getMessage());
        }
    }

    /**
     * 生成租金账单
     * @param contractId 合同ID
     * @return 生成结果
     */
    @PostMapping("/generate-bill/{contractId}")
    public Result<Void> generateRentBill(@PathVariable Long contractId) {
        try {
            boolean success = rentPaymentService.generateRentBill(contractId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("生成租金账单失败，请检查合同状态");
            }
        } catch (Exception e) {
            return Result.error("生成租金账单异常: " + e.getMessage());
        }
    }

    /**
     * 获取支付记录详情
     * @param paymentId 支付记录ID
     * @return 支付记录详情
     */
    @GetMapping("/{paymentId}")
    public Result<Map<String, Object>> getPaymentDetail(@PathVariable Long paymentId) {
        try {
            Map<String, Object> paymentDetail = rentPaymentService.getPaymentDetail(paymentId);
            if (paymentDetail != null) {
                return Result.success(paymentDetail);
            } else {
                return Result.error("支付记录不存在");
            }
        } catch (Exception e) {
            return Result.error("获取支付记录详情异常: " + e.getMessage());
        }
    }
} 