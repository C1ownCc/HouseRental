package com.house.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.entity.House;
import com.house.rental.entity.User;
import com.house.rental.service.HouseService;
import com.house.rental.common.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {
    
    @Autowired
    private HouseService houseService;
    
    @GetMapping("/list")
    public Result list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Long landlordId
    ) {
        Page<House> houses = houseService.getHouseList(page, size, keyword, province, city, district, type, status, minPrice, maxPrice, landlordId);
        return Result.success(houses);
    }
    
    @GetMapping("/recommend")
    public Result recommend() {
        List<House> houses = houseService.getRecommendHouses();
        return Result.success(houses);
    }
    
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        House house = houseService.getById(id);
        if (house == null) {
            return Result.error("房源不存在");
        }
        return Result.success(house);
    }
    
    @PostMapping
    public Result add(@RequestBody House house, HttpSession session) {
        try {
            // 从session获取当前用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return Result.error("未登录");
            }
            
            // 设置房源所有者ID
            house.setOwnerId(currentUser.getId());
            
            // 验证经纬度
            if (house.getLatitude() == null || house.getLongitude() == null) {
                return Result.error("地址解析失败，请确保地址正确");
            }
            
            houseService.addHouse(house);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody House house) {
        try {
            // 验证经纬度
            if (house.getLatitude() == null || house.getLongitude() == null) {
                return Result.error("地址解析失败，请确保地址正确");
            }
            
            house.setId(id);
            houseService.updateHouse(house);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            houseService.deleteHouse(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/batch-delete")
    public Result batchDelete(@RequestBody List<Long> ids) {
        try {
            houseService.batchDeleteHouses(ids);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/online")
    public Result online(@PathVariable Long id) {
        try {
            houseService.onlineHouse(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("上架失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/offline")
    public Result offline(@PathVariable Long id) {
        try {
            houseService.offlineHouse(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("下架失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/batch-online")
    public Result batchOnline(@RequestBody List<Long> ids) {
        try {
            houseService.batchOnlineHouses(ids);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("批量上架失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/batch-offline")
    public Result batchOffline(@RequestBody List<Long> ids) {
        try {
            houseService.batchOfflineHouses(ids);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("批量下架失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/public/list")
    public Result publicList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Long ownerId
    ) {
        // 创建查询条件，只查询已上架的房源
        Page<House> houses = houseService.getHouseList(page, size, keyword, province, city, district, type, 1, minPrice, maxPrice, ownerId);
        return Result.success(houses);
    }

    @GetMapping("/owner")
    public Result getOwnerHouses(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("未登录");
        }
        if (!"landlord".equals(user.getRole()) && !"admin".equals(user.getRole())) {
            return Result.error("无权限访问");
        }

        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        if ("landlord".equals(user.getRole())) {
            // 如果是房东，只能看到自己的房源
            wrapper.eq(House::getOwnerId, user.getId());
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(House::getTitle, keyword)
                    .or()
                    .like(House::getAddress, keyword)
                    .or()
                    .like(House::getDescription, keyword));
        }
        
        // 房型筛选
        if (type != null) {
            wrapper.eq(House::getRoomCount, type);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(House::getStatus, status);
        }
        
        // 只查询未删除的房源
        wrapper.eq(House::getDeleted, 0)
               .orderByDesc(House::getCreatedTime);

        Page<House> pageParam = new Page<>(page, size);
        Page<House> result = houseService.page(pageParam, wrapper);
        return Result.success(result);
    }

    @GetMapping("/map")
    public Result getMapHouses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Double swLat,
            @RequestParam(required = false) Double swLng,
            @RequestParam(required = false) Double neLat,
            @RequestParam(required = false) Double neLng
    ) {
        try {
            List<House> houses = houseService.getMapHouses(
                keyword, province, city, district, type,
                minPrice, maxPrice, swLat, swLng, neLat, neLng
            );
            return Result.success(houses);
        } catch (Exception e) {
            return Result.error("获取地图房源失败: " + e.getMessage());
        }
    }
} 