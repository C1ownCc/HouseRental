package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.rental.entity.House;
import com.house.rental.entity.User;
import com.house.rental.mapper.HouseMapper;
import com.house.rental.mapper.UserMapper;
import com.house.rental.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总房源数
        LambdaQueryWrapper<House> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(House::getDeleted, 0);
        long totalHouses = houseMapper.selectCount(totalWrapper);
        
        // 已上架房源数
        LambdaQueryWrapper<House> onlineWrapper = new LambdaQueryWrapper<>();
        onlineWrapper.eq(House::getDeleted, 0)
                    .eq(House::getStatus, 1);
        long onlineHouses = houseMapper.selectCount(onlineWrapper);
        
        // 总用户数
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getDeleted, 0);
        long totalUsers = userMapper.selectCount(userWrapper);
        
        // 今日新增房源
        LambdaQueryWrapper<House> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(House::getDeleted, 0)
                   .ge(House::getCreatedTime, LocalDate.now().atStartOfDay());
        long todayNewHouses = houseMapper.selectCount(todayWrapper);
        
        stats.put("totalHouses", totalHouses);
        stats.put("onlineHouses", onlineHouses);
        stats.put("totalUsers", totalUsers);
        stats.put("todayNewHouses", todayNewHouses);
        
        return stats;
    }

    @Override
    public Map<String, Object> getTrend() {
        Map<String, Object> trend = new HashMap<>();
        List<String> dateList = new ArrayList<>();
        List<Long> houseList = new ArrayList<>();
        List<Long> userList = new ArrayList<>();
        
        // 获取最近7天的数据
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();
            
            // 当天新增房源数
            LambdaQueryWrapper<House> houseWrapper = new LambdaQueryWrapper<>();
            houseWrapper.eq(House::getDeleted, 0)
                       .between(House::getCreatedTime, startTime, endTime);
            long houseCount = houseMapper.selectCount(houseWrapper);
            
            // 当天新增用户数
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getDeleted, 0)
                      .between(User::getCreatedTime, startTime, endTime);
            long userCount = userMapper.selectCount(userWrapper);
            
            dateList.add(date.format(formatter));
            houseList.add(houseCount);
            userList.add(userCount);
        }
        
        trend.put("dates", dateList);
        trend.put("houses", houseList);
        trend.put("users", userList);
        
        return trend;
    }

    @Override
    public Map<String, Object> getDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        // 房型分布
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getDeleted, 0);
        List<House> houses = houseMapper.selectList(wrapper);
        
        Map<Integer, Long> roomTypeMap = houses.stream()
            .collect(Collectors.groupingBy(House::getRoomCount, Collectors.counting()));
        
        List<Map<String, Object>> roomTypes = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : roomTypeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey() + "室");
            item.put("value", entry.getValue());
            roomTypes.add(item);
        }
        
        // 租金分布
        Map<String, Long> priceRangeMap = houses.stream()
            .collect(Collectors.groupingBy(house -> {
                int price = house.getPrice().intValue();
                if (price <= 1000) return "1000元以下";
                else if (price <= 2000) return "1000-2000元";
                else if (price <= 3000) return "2000-3000元";
                else if (price <= 5000) return "3000-5000元";
                else return "5000元以上";
            }, Collectors.counting()));
        
        List<Map<String, Object>> priceRanges = new ArrayList<>();
        for (Map.Entry<String, Long> entry : priceRangeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            priceRanges.add(item);
        }
        
        distribution.put("roomTypes", roomTypes);
        distribution.put("priceRanges", priceRanges);
        
        return distribution;
    }
} 