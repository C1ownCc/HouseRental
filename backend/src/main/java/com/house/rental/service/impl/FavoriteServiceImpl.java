package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.rental.entity.Favorite;
import com.house.rental.entity.House;
import com.house.rental.mapper.FavoriteMapper;
import com.house.rental.service.FavoriteService;
import com.house.rental.service.HouseService;
import com.house.rental.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final HouseService houseService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long houseId) {
        // 检查房源是否存在
        House house = houseService.getById(houseId);
        if (house == null) {
            throw new RuntimeException("房源不存在");
        }

        // 检查是否已收藏
        if (isFavorite(userId, houseId)) {
            throw new RuntimeException("已收藏该房源");
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setHouseId(houseId);
        save(favorite);

        // 发送收藏通知
        notificationService.notifyFavoriteAdded(userId, houseId, house.getTitle());
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long houseId) {
        // 检查房源是否存在
        House house = houseService.getById(houseId);
        if (house == null) {
            throw new RuntimeException("房源不存在");
        }

        // 删除收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getHouseId, houseId);
        remove(wrapper);

        // 发送取消收藏通知
        notificationService.notifyFavoriteRemoved(userId, houseId, house.getTitle());
    }

    @Override
    public List<Favorite> getUserFavorites(Long userId) {
        // 使用分页对象，但设置很大的页面大小来获取所有数据
        Page<Favorite> page = new Page<>(1, 1000);
        return baseMapper.getFavoriteList(page, userId).getRecords();
    }

    @Override
    public boolean isFavorite(Long userId, Long houseId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getHouseId, houseId);
        return count(wrapper) > 0;
    }
} 