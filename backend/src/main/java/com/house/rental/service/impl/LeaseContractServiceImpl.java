package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.rental.entity.House;
import com.house.rental.entity.LeaseContract;
import com.house.rental.entity.Message;
import com.house.rental.entity.User;
import com.house.rental.mapper.LeaseContractMapper;
import com.house.rental.service.HouseService;
import com.house.rental.service.LeaseContractService;
import com.house.rental.service.MessageService;
import com.house.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 租赁合同服务实现类
 */
@Service
public class LeaseContractServiceImpl extends ServiceImpl<LeaseContractMapper, LeaseContract> implements LeaseContractService {

    @Autowired
    private HouseService houseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageService messageService;

    @Override
    @Transactional
    public boolean createContract(LeaseContract leaseContract) {
        // 生成合同编号
        String contractNumber = generateContractNumber();
        leaseContract.setContractNumber(contractNumber);
        
        // 设置初始状态为待租户签署
        leaseContract.setStatus(1);
        leaseContract.setTenantSign(0);
        leaseContract.setOwnerSign(0);
        
        // 保存合同
        boolean result = save(leaseContract);
        
        if (result) {
            // 发送消息给租户
            sendContractMessage(leaseContract.getTenantId(), 
                    "租赁合同待签署", 
                    "您有一份租赁合同等待签署，请尽快处理。", 
                    4);
            
            // 发送消息给房东
            sendContractMessage(leaseContract.getOwnerId(), 
                    "租赁合同创建通知", 
                    "您的房源已创建租赁合同，等待租户签署。", 
                    4);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean tenantSignContract(Long contractId) {
        LeaseContract contract = getById(contractId);
        if (contract == null) {
            return false;
        }
        
        // 更新租户签署状态
        contract.setTenantSign(1);
        
        // 如果房东已签署，则更新合同状态为已生效
        if (contract.getOwnerSign() == 1) {
            contract.setStatus(3);
            contract.setSignDate(LocalDateTime.now());
            
            // 更新房源状态为已租出
            House house = houseService.getById(contract.getHouseId());
            if (house != null) {
                house.setLeaseStatus(1);
                house.setStatus(2); // 已出租
                houseService.updateById(house);
            }
        } else {
            // 否则更新状态为待房东签署
            contract.setStatus(2);
            
            // 发送消息给房东
            sendContractMessage(contract.getOwnerId(), 
                    "租赁合同待签署", 
                    "租户已签署合同，请您及时确认并签署。", 
                    4);
        }
        
        return updateById(contract);
    }

    @Override
    @Transactional
    public boolean ownerSignContract(Long contractId) {
        LeaseContract contract = getById(contractId);
        if (contract == null) {
            return false;
        }
        
        // 更新房东签署状态
        contract.setOwnerSign(1);
        
        // 如果租户已签署，则更新合同状态为已生效
        if (contract.getTenantSign() == 1) {
            contract.setStatus(3);
            contract.setSignDate(LocalDateTime.now());
            
            // 更新房源状态为已租出
            House house = houseService.getById(contract.getHouseId());
            if (house != null) {
                house.setLeaseStatus(1);
                house.setStatus(2); // 已出租
                houseService.updateById(house);
            }
            
            // 发送消息给租户
            sendContractMessage(contract.getTenantId(), 
                    "租赁合同已生效", 
                    "您的租赁合同已正式生效，租期从 " + 
                            contract.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                            " 到 " + 
                            contract.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
                    4);
        } else {
            // 否则更新状态为待租户签署
            contract.setStatus(1);
            
            // 发送消息给租户
            sendContractMessage(contract.getTenantId(), 
                    "租赁合同待签署", 
                    "房东已签署合同，请您及时确认并签署。", 
                    4);
        }
        
        return updateById(contract);
    }

    @Override
    public LeaseContract getContractDetail(Long contractId) {
        return getById(contractId);
    }

    @Override
    public Page<Map<String, Object>> getTenantContractList(Long tenantId, Page<LeaseContract> page) {
        // 构建查询条件
        LambdaQueryWrapper<LeaseContract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseContract::getTenantId, tenantId)
                .eq(LeaseContract::getDeleted, 0)
                .orderByDesc(LeaseContract::getCreatedTime);
        
        // 分页查询
        Page<LeaseContract> contractPage = page(page, queryWrapper);
        
        // 构建结果，添加额外信息
        Page<Map<String, Object>> resultPage = new Page<>(
                contractPage.getCurrent(),
                contractPage.getSize(),
                contractPage.getTotal()
        );
        
        List<Map<String, Object>> resultList = contractPage.getRecords().stream()
                .map(contract -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("contract", contract);
                    
                    // 添加房源信息
                    House house = houseService.getById(contract.getHouseId());
                    if (house != null) {
                        map.put("house", house);
                    }
                    
                    // 添加房东信息
                    User owner = userService.getById(contract.getOwnerId());
                    if (owner != null) {
                        map.put("owner", owner);
                    }
                    
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        
        resultPage.setRecords(resultList);
        return resultPage;
    }

    @Override
    public Page<Map<String, Object>> getOwnerContractList(Long ownerId, Page<LeaseContract> page) {
        // 构建查询条件
        LambdaQueryWrapper<LeaseContract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseContract::getOwnerId, ownerId)
                .eq(LeaseContract::getDeleted, 0)
                .orderByDesc(LeaseContract::getCreatedTime);
        
        // 分页查询
        Page<LeaseContract> contractPage = page(page, queryWrapper);
        
        // 构建结果，添加额外信息
        Page<Map<String, Object>> resultPage = new Page<>(
                contractPage.getCurrent(),
                contractPage.getSize(),
                contractPage.getTotal()
        );
        
        List<Map<String, Object>> resultList = contractPage.getRecords().stream()
                .map(contract -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("contract", contract);
                    
                    // 添加房源信息
                    House house = houseService.getById(contract.getHouseId());
                    if (house != null) {
                        map.put("house", house);
                    }
                    
                    // 添加租户信息
                    User tenant = userService.getById(contract.getTenantId());
                    if (tenant != null) {
                        map.put("tenant", tenant);
                    }
                    
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        
        resultPage.setRecords(resultList);
        return resultPage;
    }

    @Override
    @Transactional
    public boolean terminateContract(Long contractId, String remark) {
        LeaseContract contract = getById(contractId);
        if (contract == null) {
            return false;
        }
        
        // 更新合同状态为已终止
        contract.setStatus(4);
        contract.setRemark(remark);
        
        boolean result = updateById(contract);
        
        if (result) {
            // 更新房源状态为未租出
            House house = houseService.getById(contract.getHouseId());
            if (house != null) {
                house.setLeaseStatus(0);
                house.setStatus(1); // 上架
                houseService.updateById(house);
            }
            
            // 发送消息给租户
            sendContractMessage(contract.getTenantId(), 
                    "租赁合同已终止", 
                    "您的租赁合同已终止。原因：" + remark, 
                    4);
            
            // 发送消息给房东
            sendContractMessage(contract.getOwnerId(), 
                    "租赁合同已终止", 
                    "您的房源租赁合同已终止。原因：" + remark, 
                    4);
        }
        
        return result;
    }
    
    /**
     * 生成合同编号
     * @return 合同编号
     */
    private String generateContractNumber() {
        // 格式：LC-年月日-随机4位数字
        LocalDate now = LocalDate.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", (int)(Math.random() * 10000));
        return "LC-" + dateStr + "-" + randomStr;
    }
    
    /**
     * 发送合同相关消息
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型
     */
    private void sendContractMessage(Long userId, String title, String content, Integer type) {
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