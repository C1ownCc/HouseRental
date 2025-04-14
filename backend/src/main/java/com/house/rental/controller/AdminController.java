package com.house.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.rental.common.response.Result;
import com.house.rental.entity.House;
import com.house.rental.entity.LeaseContract;
import com.house.rental.entity.RentPayment;
import com.house.rental.entity.User;
import com.house.rental.service.HouseService;
import com.house.rental.service.LeaseContractService;
import com.house.rental.service.RentPaymentService;
import com.house.rental.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理控制器
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private LeaseContractService leaseContractService;
    
    @Autowired
    private RentPaymentService rentPaymentService;
    
    @Autowired
    private HouseService houseService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取系统基本统计数据
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 房源统计
            LambdaQueryWrapper<House> houseQuery = new LambdaQueryWrapper<>();
            houseQuery.eq(House::getDeleted, 0);
            long totalHouses = houseService.count(houseQuery);
            stats.put("totalHouses", totalHouses);
            
            // 在线房源统计
            LambdaQueryWrapper<House> onlineQuery = new LambdaQueryWrapper<>();
            onlineQuery.eq(House::getDeleted, 0)
                    .eq(House::getStatus, 1); // 状态1为上架
            long onlineHouses = houseService.count(onlineQuery);
            stats.put("onlineHouses", onlineHouses);
            
            // 用户统计
            LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(User::getDeleted, 0);
            long totalUsers = userService.count(userQuery);
            stats.put("totalUsers", totalUsers);
            
            // 今日新增房源
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            LambdaQueryWrapper<House> todayQuery = new LambdaQueryWrapper<>();
            todayQuery.eq(House::getDeleted, 0)
                    .between(House::getCreatedTime, todayStart, todayEnd);
            long todayNewHouses = houseService.count(todayQuery);
            stats.put("todayNewHouses", todayNewHouses);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取基本统计数据异常", e);
            return Result.error("获取基本统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取合同相关统计数据
     * @return 合同统计数据
     */
    @GetMapping("/contract/stats")
    public Result<Map<String, Object>> getContractStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取合同总数
            LambdaQueryWrapper<LeaseContract> contractQuery = new LambdaQueryWrapper<>();
            contractQuery.eq(LeaseContract::getDeleted, 0);
            long totalContracts = leaseContractService.count(contractQuery);
            stats.put("totalContracts", totalContracts);
            
            // 获取活跃合同数（已生效）
            LambdaQueryWrapper<LeaseContract> activeQuery = new LambdaQueryWrapper<>();
            activeQuery.eq(LeaseContract::getDeleted, 0)
                    .eq(LeaseContract::getStatus, 3); // 状态3为已生效
            long activeContracts = leaseContractService.count(activeQuery);
            stats.put("activeContracts", activeContracts);
            
            // 获取待确认合同数
            LambdaQueryWrapper<LeaseContract> pendingQuery = new LambdaQueryWrapper<>();
            pendingQuery.eq(LeaseContract::getDeleted, 0)
                    .eq(LeaseContract::getStatus, 1); // 状态1为待确认
            long pendingContracts = leaseContractService.count(pendingQuery);
            stats.put("pendingContracts", pendingContracts);
            
            // 获取总租金收入
            LambdaQueryWrapper<RentPayment> paidQuery = new LambdaQueryWrapper<>();
            paidQuery.eq(RentPayment::getDeleted, 0)
                    .eq(RentPayment::getPaymentStatus, 1); // 状态1为已支付
            List<RentPayment> paidPayments = rentPaymentService.list(paidQuery);
            double totalRent = paidPayments.stream()
                    .mapToDouble(payment -> payment.getAmount().doubleValue())
                    .sum();
            stats.put("totalRent", totalRent);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取合同统计数据异常", e);
            return Result.error("获取合同统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取每月租金收入统计
     * @return 每月租金收入数据
     */
    @GetMapping("/contract/monthly-rent")
    public Result<Map<String, Object>> getMonthlyRentStats() {
        try {
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            
            // 查询今年已支付的租金记录
            LambdaQueryWrapper<RentPayment> query = new LambdaQueryWrapper<>();
            query.eq(RentPayment::getDeleted, 0)
                .eq(RentPayment::getPaymentStatus, 1) // 已支付
                .isNotNull(RentPayment::getPaymentDate); // 支付日期不为空
            List<RentPayment> payments = rentPaymentService.list(query);
            
            // 按月统计租金收入
            List<Map<String, Object>> monthlyData = new ArrayList<>();
            for (int month = 1; month <= 12; month++) {
                int finalMonth = month;
                double monthlyAmount = payments.stream()
                    .filter(payment -> {
                        if (payment.getPaymentDate() == null) return false;
                        LocalDate paymentDate = payment.getPaymentDate();
                        return paymentDate.getYear() == currentYear && paymentDate.getMonthValue() == finalMonth;
                    })
                    .mapToDouble(payment -> payment.getAmount().doubleValue())
                    .sum();
                
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                monthData.put("amount", monthlyAmount);
                monthlyData.add(monthData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("data", monthlyData);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取月租金统计数据异常", e);
            return Result.error("获取月租金统计数据失败: " + e.getMessage());
        }
    }
} 