package com.house.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.common.response.Result;
import com.house.rental.entity.House;
import com.house.rental.entity.LeaseContract;
import com.house.rental.entity.User;
import com.house.rental.service.HouseService;
import com.house.rental.service.LeaseContractService;
import com.house.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租赁合同控制器
 */
@RestController
@RequestMapping("/lease-contract")
public class LeaseContractController {

    @Autowired
    private LeaseContractService leaseContractService;
    
    @Autowired
    private HouseService houseService;
    
    @Autowired
    private UserService userService;

    /**
     * 创建租赁合同
     * @param leaseContract 租赁合同信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Void> createContract(@RequestBody LeaseContract leaseContract) {
        try {
            // 检查房源是否存在
            House house = houseService.getById(leaseContract.getHouseId());
            if (house == null) {
                return Result.error("房源不存在");
            }
            
            // 检查房源是否已出租
            if (house.getLeaseStatus() != null && house.getLeaseStatus() == 1) {
                return Result.error("该房源已出租");
            }
            
            // 检查房源所有者
            if (!house.getOwnerId().equals(leaseContract.getOwnerId())) {
                return Result.error("您不是该房源的所有者");
            }
            
            boolean success = leaseContractService.createContract(leaseContract);
            if (success) {
                return Result.success();
            } else {
                return Result.error("创建租赁合同失败");
            }
        } catch (Exception e) {
            return Result.error("创建租赁合同异常: " + e.getMessage());
        }
    }

    /**
     * 租户签署合同
     * @param contractId 合同ID
     * @return 签署结果
     */
    @PutMapping("/tenant-sign/{contractId}")
    public Result<Void> tenantSignContract(@PathVariable Long contractId) {
        try {
            boolean success = leaseContractService.tenantSignContract(contractId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("签署失败，请检查合同状态");
            }
        } catch (Exception e) {
            return Result.error("签署异常: " + e.getMessage());
        }
    }

    /**
     * 房东签署合同
     * @param contractId 合同ID
     * @return 签署结果
     */
    @PutMapping("/owner-sign/{contractId}")
    public Result<Void> ownerSignContract(@PathVariable Long contractId) {
        try {
            boolean success = leaseContractService.ownerSignContract(contractId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("签署失败，请检查合同状态");
            }
        } catch (Exception e) {
            return Result.error("签署异常: " + e.getMessage());
        }
    }

    /**
     * 获取合同详情
     * @param contractId 合同ID
     * @return 合同详情
     */
    @GetMapping("/{contractId}")
    public Result<LeaseContract> getContractDetail(@PathVariable Long contractId) {
        try {
            LeaseContract contract = leaseContractService.getContractDetail(contractId);
            if (contract != null) {
                return Result.success(contract);
            } else {
                return Result.error("合同不存在");
            }
        } catch (Exception e) {
            return Result.error("获取合同详情异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询租户的合同列表
     * @param tenantId 租户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 合同列表
     */
    @GetMapping("/tenant/{tenantId}")
    public Result<Page<Map<String, Object>>> getTenantContractList(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<LeaseContract> page = new Page<>(current, size);
            Page<Map<String, Object>> contractPage = leaseContractService.getTenantContractList(tenantId, page);
            return Result.success(contractPage);
        } catch (Exception e) {
            return Result.error("获取租户合同列表异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询房东的合同列表
     * @param ownerId 房东ID
     * @param current 当前页
     * @param size 每页大小
     * @return 合同列表
     */
    @GetMapping("/owner/{ownerId}")
    public Result<Page<Map<String, Object>>> getOwnerContractList(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<LeaseContract> page = new Page<>(current, size);
            Page<Map<String, Object>> contractPage = leaseContractService.getOwnerContractList(ownerId, page);
            return Result.success(contractPage);
        } catch (Exception e) {
            return Result.error("获取房东合同列表异常: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询所有合同列表（管理员使用）
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词搜索（合同编号、房源标题、用户名）
     * @param status 合同状态
     * @return 合同列表
     */
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getAllContractList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Page<LeaseContract> page = new Page<>(current, size);
            
            // 构建查询条件
            LambdaQueryWrapper<LeaseContract> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LeaseContract::getDeleted, 0);
            
            // 按状态筛选
            if (status != null) {
                queryWrapper.eq(LeaseContract::getStatus, status);
            }
            
            // 关键词搜索（合同编号）
            if (StringUtils.hasText(keyword)) {
                queryWrapper.like(LeaseContract::getContractNumber, keyword);
            }
            
            // 按创建时间倒序排序
            queryWrapper.orderByDesc(LeaseContract::getCreatedTime);
            
            // 分页查询合同
            Page<LeaseContract> contractPage = leaseContractService.page(page, queryWrapper);
            List<Map<String, Object>> resultList = new ArrayList<>();
            
            // 封装返回结果
            for (LeaseContract contract : contractPage.getRecords()) {
                Map<String, Object> resultMap = new HashMap<>();
                
                // 添加合同信息
                resultMap.put("contract", contract);
                
                // 获取并添加房源信息
                House house = houseService.getById(contract.getHouseId());
                resultMap.put("house", house);
                
                // 获取并添加租户信息
                User tenant = userService.getById(contract.getTenantId());
                if (tenant != null) {
                    tenant.setPassword(null); // 清除敏感信息
                }
                resultMap.put("tenant", tenant);
                
                // 获取并添加房东信息
                User owner = userService.getById(contract.getOwnerId());
                if (owner != null) {
                    owner.setPassword(null); // 清除敏感信息
                }
                resultMap.put("owner", owner);
                
                resultList.add(resultMap);
            }
            
            // 构建返回的分页对象
            Page<Map<String, Object>> resultPage = new Page<>(
                    contractPage.getCurrent(), 
                    contractPage.getSize(), 
                    contractPage.getTotal());
            resultPage.setRecords(resultList);
            
            return Result.success(resultPage);
        } catch (Exception e) {
            return Result.error("获取合同列表异常: " + e.getMessage());
        }
    }

    /**
     * 终止合同
     * @param contractId 合同ID
     * @param remark 备注说明
     * @return 终止结果
     */
    @PutMapping("/terminate/{contractId}")
    public Result<Void> terminateContract(
            @PathVariable Long contractId,
            @RequestParam String remark) {
        try {
            boolean success = leaseContractService.terminateContract(contractId, remark);
            if (success) {
                return Result.success();
            } else {
                return Result.error("终止合同失败，请检查合同状态");
            }
        } catch (Exception e) {
            return Result.error("终止合同异常: " + e.getMessage());
        }
    }
} 