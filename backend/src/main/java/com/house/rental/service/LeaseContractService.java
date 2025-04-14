package com.house.rental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.house.rental.entity.LeaseContract;

import java.util.List;
import java.util.Map;

/**
 * 租赁合同服务接口
 */
public interface LeaseContractService extends IService<LeaseContract> {
    
    /**
     * 创建租赁合同
     * @param leaseContract 租赁合同信息
     * @return 创建结果
     */
    boolean createContract(LeaseContract leaseContract);
    
    /**
     * 租户签署合同
     * @param contractId 合同ID
     * @return 签署结果
     */
    boolean tenantSignContract(Long contractId);
    
    /**
     * 房东签署合同
     * @param contractId 合同ID
     * @return 签署结果
     */
    boolean ownerSignContract(Long contractId);
    
    /**
     * 获取合同详情
     * @param contractId 合同ID
     * @return 合同详情
     */
    LeaseContract getContractDetail(Long contractId);
    
    /**
     * 分页查询租户的合同列表
     * @param tenantId 租户ID
     * @param page 分页参数
     * @return 合同列表
     */
    Page<Map<String, Object>> getTenantContractList(Long tenantId, Page<LeaseContract> page);
    
    /**
     * 分页查询房东的合同列表
     * @param ownerId 房东ID
     * @param page 分页参数
     * @return 合同列表
     */
    Page<Map<String, Object>> getOwnerContractList(Long ownerId, Page<LeaseContract> page);
    
    /**
     * 终止合同
     * @param contractId 合同ID
     * @param remark 备注说明
     * @return 终止结果
     */
    boolean terminateContract(Long contractId, String remark);
} 