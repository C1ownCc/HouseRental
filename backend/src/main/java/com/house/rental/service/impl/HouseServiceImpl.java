package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.rental.common.exception.BusinessException;
import com.house.rental.entity.House;
import com.house.rental.mapper.HouseMapper;
import com.house.rental.service.HouseService;
import com.house.rental.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    private final NotificationService notificationService;

    @Override
    public Page<House> getHouseList(Integer page, Integer size, String keyword, String province, String city, String district, Integer type, Integer status, Integer minPrice, Integer maxPrice, Long landlordId) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        
        // 添加搜索条件
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(House::getTitle, keyword)
                    .or()
                    .like(House::getAddress, keyword)
                    .or()
                    .like(House::getDescription, keyword);
        }
        
        // 区域筛选 - 省市区
        if (province != null && !province.isEmpty()) {
            wrapper.eq(House::getProvince, province);
        }
        if (city != null && !city.isEmpty()) {
            wrapper.eq(House::getCity, city);
        }
        if (district != null && !district.isEmpty()) {
            wrapper.eq(House::getDistrict, district);
        }
        
        // 房型筛选
        if (type != null) {
            wrapper.eq(House::getRoomCount, type);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(House::getStatus, status);
        }
        
        // 价格区间筛选
        if (minPrice != null) {
            wrapper.ge(House::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(House::getPrice, maxPrice);
        }
        
        // 房东筛选
        if (landlordId != null) {
            wrapper.eq(House::getOwnerId, landlordId);
        }
        
        // 只查询未删除的房源
        wrapper.eq(House::getDeleted, 0);
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(House::getCreatedTime);
        
        return page(pageParam, wrapper);
    }

    @Override
    public List<House> getRecommendHouses() {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getDeleted, 0)
                .eq(House::getStatus, 1)
                .orderByDesc(House::getCreatedTime)
                .last("LIMIT 4");
        return list(wrapper);
    }

    @Override
    @Transactional
    public void addHouse(House house) {
        // 设置默认值
        if (house.getBathroomCount() == null) {
            house.setBathroomCount(1);  // 默认1个卫生间
        }
        if (house.getDeposit() == null) {
            house.setDeposit(house.getPrice());  // 默认押金等于月租金
        }
        if (house.getTotalFloor() == null) {
            house.setTotalFloor(1);  // 默认总楼层为1
        }
        if (house.getHasElevator() == null) {
            house.setHasElevator(0);  // 默认无电梯
        }
        if (house.getDecoration() == null) {
            house.setDecoration("简装");  // 默认简装
        }
        if (house.getFacilities() == null) {
            house.setFacilities("");  // 默认无设施
        }
        if (house.getContactName() == null) {
            house.setContactName("");  // 默认空字符串
        }
        if (house.getContactPhone() == null) {
            house.setContactPhone("");  // 默认空字符串
        }
        // 设置地区默认值
        if (house.getProvince() == null) {
            house.setProvince("未知");  // 默认省份
        }
        if (house.getCity() == null) {
            house.setCity("未知");  // 默认城市
        }
        if (house.getDistrict() == null) {
            house.setDistrict("未知");  // 默认区县
        }
        
        // 验证经纬度
        if (house.getLatitude() == null || house.getLongitude() == null) {
            throw new BusinessException("地址解析失败，请确保地址正确");
        }
        
        house.setStatus(0);  // 默认下架状态
        house.setDeleted(0); // 未删除
        
        save(house);
    }

    @Override
    @Transactional
    public void updateHouse(House house) {
        // 获取原房源信息
        House oldHouse = getById(house.getId());
        if (oldHouse == null) {
            throw new BusinessException("房源不存在");
        }
        
        // 验证经纬度
        if (house.getLatitude() == null || house.getLongitude() == null) {
            throw new BusinessException("地址解析失败，请确保地址正确");
        }
        
        // 更新房源信息
        updateById(house);
        
        // 如果状态发生变化，通知收藏用户
        if (oldHouse.getStatus() != null && house.getStatus() != null && 
            !oldHouse.getStatus().equals(house.getStatus())) {
            String statusText = getStatusText(house.getStatus());
            notificationService.notifyHouseStatusChange(house.getId(), statusText);
        }
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        // 获取原房源信息，确保房源存在
        House dbHouse = getById(id);
        if (dbHouse == null) {
            throw new RuntimeException("房源不存在");
        }
        
        // 直接删除房源
        int result = baseMapper.deleteById(id);
        if (result != 1) {
            throw new RuntimeException("删除房源失败");
        }
        
        // 通知收藏用户房源已删除
        notificationService.notifyHouseStatusChange(id, "房源已删除");
    }

    @Override
    @Transactional
    public void batchDeleteHouses(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        for (Long id : ids) {
            try {
                deleteHouse(id);
            } catch (Exception e) {
                log.error("批量删除房源失败，ID: " + id, e);
                // 继续处理其他房源
            }
        }
    }

    @Override
    @Transactional
    public void onlineHouse(Long id) {
        House house = new House();
        house.setId(id);
        house.setStatus(1);
        updateById(house);
        
        // 通知收藏用户房源已上线
        notificationService.notifyHouseStatusChange(id, "房源已上线");
    }

    @Override
    @Transactional
    public void offlineHouse(Long id) {
        House house = new House();
        house.setId(id);
        house.setStatus(0);
        updateById(house);
        
        // 通知收藏用户房源已下线
        notificationService.notifyHouseStatusChange(id, "房源已下线");
    }

    @Override
    @Transactional
    public void batchOnlineHouses(List<Long> ids) {
        for (Long id : ids) {
            onlineHouse(id);
        }
    }

    @Override
    @Transactional
    public void batchOfflineHouses(List<Long> ids) {
        for (Long id : ids) {
            offlineHouse(id);
        }
    }
    
    /**
     * 获取房源状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "已下线";
            case 1: return "已上线";
            default: return "状态未知";
        }
    }

    @Override
    public List<House> getMapHouses(
            String keyword, String province, String city, String district,
            Integer type, Integer minPrice, Integer maxPrice,
            Double swLat, Double swLng, Double neLat, Double neLng
    ) {
        LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
        
        // 基本条件：未删除且上架状态
        queryWrapper.eq(House::getDeleted, 0)
                   .eq(House::getStatus, 1);

        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(House::getTitle, keyword)
                .or()
                .like(House::getAddress, keyword)
            );
        }

        // 区域筛选
        if (StringUtils.isNotBlank(province)) {
            queryWrapper.eq(House::getProvince, province);
        }
        if (StringUtils.isNotBlank(city)) {
            queryWrapper.eq(House::getCity, city);
        }
        if (StringUtils.isNotBlank(district)) {
            queryWrapper.eq(House::getDistrict, district);
        }

        // 房型筛选
        if (type != null) {
            queryWrapper.eq(House::getRoomCount, type);
        }

        // 价格区间筛选
        if (minPrice != null) {
            queryWrapper.ge(House::getPrice, minPrice);
        }
        if (maxPrice != null) {
            queryWrapper.le(House::getPrice, maxPrice);
        }

        // 地图范围筛选
        if (swLat != null && swLng != null && neLat != null && neLng != null) {
            queryWrapper.ge(House::getLatitude, swLat)
                       .le(House::getLatitude, neLat)
                       .ge(House::getLongitude, swLng)
                       .le(House::getLongitude, neLng);
        }

        // 按更新时间倒序排序
        queryWrapper.orderByDesc(House::getUpdateTime);

        return baseMapper.selectList(queryWrapper);
    }
} 