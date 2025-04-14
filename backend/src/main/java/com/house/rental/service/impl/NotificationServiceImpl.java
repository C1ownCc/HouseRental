package com.house.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.rental.entity.Favorite;
import com.house.rental.entity.House;
import com.house.rental.entity.Message;
import com.house.rental.mapper.FavoriteMapper;
import com.house.rental.mapper.HouseMapper;
import com.house.rental.mapper.MessageMapper;
import com.house.rental.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final MessageMapper messageMapper;
    private final FavoriteMapper favoriteMapper;
    private final HouseMapper houseMapper;

    @Override
    @Transactional
    public void notifyFavoriteAdded(Long userId, Long houseId, String houseTitle) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle("收藏提醒");
        message.setContent("您已成功收藏房源：" + houseTitle);
        message.setType(3); // 收藏消息
        message.setStatus(0); // 未读
        message.setDeleted(0);
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void notifyFavoriteRemoved(Long userId, Long houseId, String houseTitle) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle("取消收藏提醒");
        message.setContent("您已取消收藏房源：" + houseTitle);
        message.setType(3); // 收藏消息
        message.setStatus(0); // 未读
        message.setDeleted(0);
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void notifyHouseStatusChange(Long houseId, String statusText) {
        // 获取房源信息
        House house = houseMapper.selectById(houseId);
        if (house == null) {
            return;
        }

        // 获取所有收藏该房源的用户
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getHouseId, houseId);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);

        // 给每个收藏用户发送消息
        for (Favorite favorite : favorites) {
            Message message = new Message();
            message.setUserId(favorite.getUserId());
            message.setTitle("房源状态变更提醒");
            message.setContent("您收藏的房源：" + house.getTitle() + " " + statusText);
            message.setType(3); // 收藏消息
            message.setStatus(0); // 未读
            message.setDeleted(0);
            messageMapper.insert(message);
        }
    }
} 