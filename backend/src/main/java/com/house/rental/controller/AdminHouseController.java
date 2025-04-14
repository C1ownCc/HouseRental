package com.house.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.entity.House;
import com.house.rental.service.HouseService;
import com.house.rental.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/house")
public class AdminHouseController {
    
    @Autowired
    private HouseService houseService;
    
    @GetMapping("/list")
    public Result list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer roomCount,
            @RequestParam(required = false) Integer status) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        
        // 添加搜索条件
        if (title != null && !title.isEmpty()) {
            wrapper.like(House::getTitle, title);
        }
        if (area != null && !area.isEmpty()) {
            wrapper.eq(House::getDistrict, area);
        }
        if (roomCount != null) {
            wrapper.eq(House::getRoomCount, roomCount);
        }
        if (status != null) {
            wrapper.eq(House::getStatus, status);
        }
        
        // 只查询未删除的房源
        wrapper.eq(House::getDeleted, 0);
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(House::getCreatedTime);
        
        Page<House> result = houseService.page(pageParam, wrapper);
        return Result.success(result);
    }
    
    @PostMapping
    public Result add(@RequestBody House house) {
        // 设置初始状态为下架
        house.setStatus(0);  // 0: 下架, 1: 上架, 2: 已出租
        house.setDeleted(0);
        boolean success = houseService.save(house);
        return success ? Result.success(null) : Result.error("添加失败");
    }
    
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody House house) {
        house.setId(id);
        boolean success = houseService.updateById(house);
        return success ? Result.success(null) : Result.error("更新失败");
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
        House house = new House();
        house.setId(id);
        house.setStatus(1);  // 1: 上架
        boolean success = houseService.updateById(house);
        return success ? Result.success(null) : Result.error("上架失败");
    }
    
    @PutMapping("/{id}/offline")
    public Result offline(@PathVariable Long id) {
        House house = new House();
        house.setId(id);
        house.setStatus(0);  // 0: 下架
        boolean success = houseService.updateById(house);
        return success ? Result.success(null) : Result.error("下架失败");
    }
    
    @PutMapping("/batch/online")
    public Result batchOnline(@RequestBody List<Long> ids) {
        boolean success = true;
        for (Long id : ids) {
            House house = new House();
            house.setId(id);
            house.setStatus(1);  // 1: 上架
            success = success && houseService.updateById(house);
        }
        return success ? Result.success(null) : Result.error("批量上架失败");
    }
    
    @PutMapping("/batch/offline")
    public Result batchOffline(@RequestBody List<Long> ids) {
        boolean success = true;
        for (Long id : ids) {
            House house = new House();
            house.setId(id);
            house.setStatus(0);  // 0: 下架
            success = success && houseService.updateById(house);
        }
        return success ? Result.success(null) : Result.error("批量下架失败");
    }
} 