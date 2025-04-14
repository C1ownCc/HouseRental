package com.house.rental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.house.rental.entity.House;

import java.util.List;

public interface HouseService extends IService<House> {
    
    /**
     * 获取房源列表
     */
    Page<House> getHouseList(Integer page, Integer size, String keyword, String province, String city, String district, Integer type, Integer status, Integer minPrice, Integer maxPrice, Long landlordId);

    /**
     * 获取推荐房源
     */
    List<House> getRecommendHouses();

    /**
     * 添加房源
     */
    void addHouse(House house);

    /**
     * 更新房源
     */
    void updateHouse(House house);

    /**
     * 删除房源
     */
    void deleteHouse(Long id);

    /**
     * 批量删除房源
     */
    void batchDeleteHouses(List<Long> ids);

    /**
     * 上架房源
     */
    void onlineHouse(Long id);

    /**
     * 下架房源
     */
    void offlineHouse(Long id);

    /**
     * 批量上架房源
     */
    void batchOnlineHouses(List<Long> ids);

    /**
     * 批量下架房源
     */
    void batchOfflineHouses(List<Long> ids);

    List<House> getMapHouses(
        String keyword, String province, String city, String district,
        Integer type, Integer minPrice, Integer maxPrice,
        Double swLat, Double swLng, Double neLat, Double neLng
    );
} 